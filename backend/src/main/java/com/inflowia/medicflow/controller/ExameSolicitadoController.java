package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.exame.ExameSolicitadoDetailsDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoMinDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoPatchDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoUpdateDTO;
import com.inflowia.medicflow.service.ExameSolicitadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/exames-solicitados")
public class ExameSolicitadoController {

    @Autowired
    private ExameSolicitadoService service;

    // CREATE
    @PostMapping
    @PreAuthorize("hasAuthority('exames-solicitados:write')")
    public ResponseEntity<ExameSolicitadoDetailsDTO> inserir(
            @Valid @RequestBody ExameSolicitadoUpdateDTO dto) {

        ExameSolicitadoDetailsDTO created = service.inserir(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('exames-solicitados:write')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // FIND BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('exames-solicitados:read')")
    public ResponseEntity<ExameSolicitadoDetailsDTO> buscarPorId(@PathVariable Long id) {
        ExameSolicitadoDetailsDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('exames-solicitados:write')")
    public ResponseEntity<ExameSolicitadoDetailsDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ExameSolicitadoPatchDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('exames-solicitados:write')")
    public ResponseEntity<ExameSolicitadoDetailsDTO> atualizarParcialmente(
            @PathVariable Long id,
            @RequestBody @Valid ExameSolicitadoPatchDTO dto) {

        return ResponseEntity.ok(service.atualizarParcialmente(id, dto));
    }

    // LISTAR EXAMES POR CONSULTA
    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasAuthority('exames-solicitados:read')")
    public ResponseEntity<Page<ExameSolicitadoMinDTO>> listarPorConsulta(
            @PathVariable Long consultaId,
            Pageable pageable) {

        Page<ExameSolicitadoMinDTO> page = service.listarPorConsulta(consultaId, pageable);
        return ResponseEntity.ok(page);
    }

    // LISTAR EXAMES POR EXAME BASE
    @GetMapping("/exame-base/{exameBaseId}")
    @PreAuthorize("hasAuthority('exames-solicitados:read')")
    public ResponseEntity<Page<ExameSolicitadoMinDTO>> listarPorExameBase(
            @PathVariable Long exameBaseId,
            Pageable pageable) {

        Page<ExameSolicitadoMinDTO> page = service.listarPorExameBase(exameBaseId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAuthority('exames-solicitados:read')")
    public ResponseEntity<Page<ExameSolicitadoMinDTO>> listarPorPaciente(
            @PathVariable Long pacienteId,
            Pageable pageable) {

        Page<ExameSolicitadoMinDTO> page = service.listarPorPaciente(pacienteId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/paciente/{pacienteId}/ultima-consulta")
    @PreAuthorize("hasAuthority('exames-solicitados:read')")
    public ResponseEntity<Page<ExameSolicitadoMinDTO>> listarPorPacienteUltimaConsulta(
            @PathVariable Long pacienteId,
            Pageable pageable) {

        Page<ExameSolicitadoMinDTO> page =
                service.listarPorPacienteUltimaConsulta(pacienteId, pageable);
        return ResponseEntity.ok(page);
    }
}
