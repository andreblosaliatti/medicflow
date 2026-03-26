package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteListDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProntuarioDetailsDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProfileDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import com.inflowia.medicflow.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    @PostMapping
    @PreAuthorize("hasAuthority('pacientes:write')")
    public ResponseEntity<PacienteDTO> cadastrar(
            @RequestBody @Valid PacienteDTO dto,
            UriComponentsBuilder uriBuilder) {

        PacienteDTO salvo = service.cadastrar(dto);

        URI uri = uriBuilder
                .path("/pacientes/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(uri).body(salvo);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('pacientes:read')")
    public ResponseEntity<Page<PacienteListDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false, name = "convenio") String convenio,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<PacienteListDTO> page = service.listar(nome, cpf, ativo, convenio, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/inativos")
    @PreAuthorize("hasAuthority('pacientes:read')")
    public ResponseEntity<Page<PacienteListDTO>> listarInativos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false, name = "convenio") String convenio,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(service.listarInativos(nome, cpf, convenio, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('pacientes:read')")
    public ResponseEntity<PacienteDTO> buscarPorId(@PathVariable Long id) {
        PacienteDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/perfil")
    @PreAuthorize("hasAuthority('pacientes:read')")
    public ResponseEntity<PacienteProfileDTO> buscarPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPerfil(id));
    }

    @GetMapping("/{id}/prontuario")
    @PreAuthorize("hasAuthority('pacientes:read')")
    public ResponseEntity<PacienteProntuarioDetailsDTO> buscarProntuario(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarProntuario(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('pacientes:write')")
    public ResponseEntity<PacienteDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PacienteUpdateDTO dto) {

        PacienteDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('pacientes:write')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAuthority('pacientes:write')")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
