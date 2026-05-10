package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import com.inflowia.medicflow.service.MedicamentoService;
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
@RequestMapping(ApiPaths.MEDICAMENTOS)
public class MedicamentoController {

    @Autowired
    private MedicamentoService service;

    @GetMapping
    @PreAuthorize("hasAuthority('medicamentos:read')")
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>> findAll(
            @RequestParam(name = "nome", defaultValue = "") String nome,
            Pageable pageable
    ) {
        Page<MedicamentoPrescritoMinDTO> dto = service.buscaPorNome(nome, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAuthority('medicamentos:read')")
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>> listarHistoricoPorPaciente(
            @PathVariable Long pacienteId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarHistoricoPorPaciente(pacienteId, pageable));
    }

    @PostMapping("/consultas/{consultaId}")
    @PreAuthorize("hasAuthority('medicamentos:write')")
    public ResponseEntity<MedicamentoPrescritoMinDTO> adicionar(
            @PathVariable Long consultaId,
            @RequestBody @Valid MedicamentoPrescritoDTO dados
    ) {
        MedicamentoPrescritoMinDTO dto = service.adicionarMedicamento(consultaId, dados);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{medicamentoId}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{medicamentoId}")
    @PreAuthorize("hasAuthority('medicamentos:write')")
    public ResponseEntity<MedicamentoPrescritoMinDTO> atualizar(
            @PathVariable Long medicamentoId,
            @RequestBody @Valid MedicamentoPrescritoDTO dados
    ) {
        return ResponseEntity.ok(service.atualizarMedicamento(medicamentoId, dados));
    }

    @GetMapping("/consultas/{consultaId}")
    @PreAuthorize("hasAuthority('medicamentos:read')")
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>> listarPorConsulta(
            @PathVariable Long consultaId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPorConsulta(consultaId, pageable));
    }

    @GetMapping("/{medicamentoId}")
    @PreAuthorize("hasAuthority('medicamentos:read')")
    public ResponseEntity<MedicamentoPrescritoMinDTO> findById(@PathVariable Long medicamentoId) {
        MedicamentoPrescritoMinDTO dto = service.findById(medicamentoId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{medicamentoId}")
    @PreAuthorize("hasAuthority('medicamentos:write')")
    public ResponseEntity<Void> remover(@PathVariable Long medicamentoId) {
        service.delete(medicamentoId);
        return ResponseEntity.noContent().build();
    }
}
