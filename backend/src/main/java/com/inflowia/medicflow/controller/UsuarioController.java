package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.dto.usuario.DadosAtualizacaoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosListagemUsuario;
import com.inflowia.medicflow.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(ApiPaths.USUARIOS)
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @PreAuthorize("hasAuthority('usuarios:read')")
    public ResponseEntity<Page<DadosListagemUsuario>> findAll(
            @RequestParam(value = "nome", defaultValue = "") String nome,
            @RequestParam(value = "ativo", required = false) Boolean ativo,
            @RequestParam(value = "role", required = false) String role,
            Pageable pageable) {

        Page<DadosListagemUsuario> page = service.findAllPaged(nome, ativo, role, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('usuarios:read')")
    public ResponseEntity<DadosDetalhamentoUsuario> findById(@PathVariable Long id) {
        DadosDetalhamentoUsuario dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasAuthority('usuarios:read')")
    public ResponseEntity<DadosDetalhamentoUsuario> findByCpf(@PathVariable String cpf) {
        DadosDetalhamentoUsuario dto = service.findByCpf(cpf);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('usuarios:write')")
    public ResponseEntity<DadosDetalhamentoUsuario> insert(
            @RequestBody @Valid DadosCadastroUsuario dto) {

        DadosDetalhamentoUsuario created = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('usuarios:write')")
    public ResponseEntity<DadosDetalhamentoUsuario> update(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoUsuario dto) {

        DadosDetalhamentoUsuario updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('usuarios:write')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
