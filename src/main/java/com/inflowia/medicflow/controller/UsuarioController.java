package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.usuario.DadosAtualizacaoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosListagemUsuario;
import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoUsuario> criar(@RequestBody @Valid DadosCadastroUsuario dados) {
        if (repository.existsByLoginIgnoreCase(dados.login()) ||
            repository.existsByEmailIgnoreCase(dados.email())) {
            return ResponseEntity.status(409).build(); // CONFLICT
        }

        Usuario usuario = Usuario.builder()
                .login(dados.login())
                .senha(encoder.encode(dados.senha()))
                .nome(dados.nome())
                .sobrenome(dados.sobrenome())
                .email(dados.email())
                .cpf(dados.cpf())
                .perfil(dados.perfil())
                .endereco(dados.endereco() == null ? null : dados.endereco().toEntity())
                .ativo(true)
                .build();

        Usuario salvo = repository.save(usuario);
        return ResponseEntity.created(URI.create("/usuarios/" + salvo.getId()))
                .body(new DadosDetalhamentoUsuario(salvo));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        Page<DadosListagemUsuario> page = repository.findAll(pageable).map(DadosListagemUsuario::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoUsuario> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(u -> ResponseEntity.ok(new DadosDetalhamentoUsuario(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<DadosDetalhamentoUsuario> atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        Usuario u = repository.findById(dados.id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dados.email() != null &&
            repository.existsByEmailIgnoreCaseAndIdNot(dados.email(), dados.id())) {
            return ResponseEntity.status(409).build();
        }

        u.atualizar(
                dados.nome(),
                dados.sobrenome(),
                dados.email(),
                dados.perfil(),
                dados.ativo(),
                dados.endereco() == null ? null : dados.endereco().toEntity()
        );

        Usuario salvo = repository.save(u);
        return ResponseEntity.ok(new DadosDetalhamentoUsuario(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setAtivo(false);
        repository.save(u);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
