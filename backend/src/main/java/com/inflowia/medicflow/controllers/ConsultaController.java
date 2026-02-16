package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.consulta.ConsultaDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDetailsDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaMinDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaUpdateDTO;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.services.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConsultaDetailsDTO> criar(
            @RequestBody @Valid ConsultaDTO dto,
            UriComponentsBuilder uriBuilder) {

        ConsultaDetailsDTO created = service.criar(dto);
        var uri = uriBuilder.path("/consultas/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaDetailsDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ConsultaUpdateDTO dto) {

        ConsultaDetailsDTO updated = service.atualizar(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetailsDTO> buscarPorId(@PathVariable Long id) {
        ConsultaDetailsDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ConsultaMinDTO>> listar(Pageable pageable) {
        Page<ConsultaMinDTO> page = service.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<Page<ConsultaMinDTO>> listarPorPaciente(
            @PathVariable Long pacienteId,
            Pageable pageable) {

        Page<ConsultaMinDTO> page = service.listarPorPaciente(pacienteId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/paciente/{pacienteId}/ultima")
    public ResponseEntity<ConsultaDetailsDTO> buscarUltimaConsultaPorPaciente(
            @PathVariable Long pacienteId) {

        ConsultaDetailsDTO dto = service.buscarUltimaConsultaPorPaciente(pacienteId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<Page<ConsultaMinDTO>> listarPorMedico(
            @PathVariable Long medicoId,
            Pageable pageable) {

        Page<ConsultaMinDTO> page = service.listarPorMedico(medicoId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ConsultaMinDTO>> listarPorStatus(
            @RequestParam StatusConsulta status) {
        List<ConsultaMinDTO> list = service.listarPorStatus(status);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ConsultaMinDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        List<ConsultaMinDTO> list = service.listarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(list);
    }

}
