package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import com.inflowia.medicflow.services.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    // POST - cadastrar
    @PostMapping
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

    // GET - listar
    @GetMapping
    public ResponseEntity<Page<PacienteMinDTO>> listar(
            @PageableDefault(size = 10, sort = "primeiroNome") Pageable pageable) {

        Page<PacienteMinDTO> page = service.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/inativos")
    public ResponseEntity<Page<PacienteMinDTO>> listarInativos(
            @PageableDefault(size = 10, sort = "primeiroNome") Pageable pageable) {

        return ResponseEntity.ok(service.listarInativos(pageable));
    }

    // GET - buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> buscarPorId(@PathVariable Long id) {
        PacienteDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    // PUT - atualizar
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PacienteUpdateDTO dto) {

        PacienteDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    // DELETE - desativar (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
