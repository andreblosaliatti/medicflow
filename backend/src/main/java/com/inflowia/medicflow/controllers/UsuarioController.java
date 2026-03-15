package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.usuario.DadosAtualizacaoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosListagemUsuario;
import com.inflowia.medicflow.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // ------------------------------------------------------------
    // LISTAR USUÁRIOS POR NOME (PAGINADO)
    // /usuarios?nome=greg
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<Page<DadosListagemUsuario>> findAll(
            @RequestParam(value = "nome", defaultValue = "") String nome,
            Pageable pageable) {

        Page<DadosListagemUsuario> page = service.findAllPaged(nome, pageable);
        return ResponseEntity.ok(page);
    }

    // ------------------------------------------------------------
    // BUSCAR POR ID
    // ------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoUsuario> findById(@PathVariable Long id) {
        DadosDetalhamentoUsuario dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    // ------------------------------------------------------------
    // BUSCAR POR CPF
    // /usuarios/cpf/39053344705
    // ------------------------------------------------------------
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<DadosDetalhamentoUsuario> findByCpf(@PathVariable String cpf) {
        DadosDetalhamentoUsuario dto = service.findByCpf(cpf);
        return ResponseEntity.ok(dto);
    }

    // ------------------------------------------------------------
    // INSERT
    // ------------------------------------------------------------
    @PostMapping
    public ResponseEntity<DadosDetalhamentoUsuario> insert(
            @RequestBody @Valid DadosCadastroUsuario dto) {

        DadosDetalhamentoUsuario created = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<DadosAtualizacaoUsuario> update(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoUsuario dto) {

        DadosAtualizacaoUsuario updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
