package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import com.inflowia.medicflow.services.MedicamentoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService service;

    @GetMapping
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>>
    findAll(@RequestParam(name = "nome", defaultValue = "") String name, Pageable pageable) {
        Page<MedicamentoPrescritoMinDTO> dto = service.buscaPorNome(name, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>> findByPacienteId(@PathVariable Long pacienteId, Pageable pageable) {
        return ResponseEntity.ok(service.findByPaciente(pacienteId, pageable));
    }

    @PostMapping("/consultas/{id}")
    public ResponseEntity<MedicamentoPrescritoMinDTO> adicionar(@PathVariable Long id, @RequestBody @Valid MedicamentoPrescritoDTO dados){
        MedicamentoPrescritoMinDTO dto = service.adicionarMedicamento(id, dados);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("/consultas/{id}")
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>> listar(@PathVariable Long id, Pageable pageable){
        return ResponseEntity.ok(service.listarMedicamentos(id,  pageable));
    }

    @GetMapping(value = "/{medicamentoId}")
    public ResponseEntity<MedicamentoPrescritoMinDTO> findById(@PathVariable Long medicamentoId){
        MedicamentoPrescritoMinDTO dto = service.findById(medicamentoId);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{medicamentoId}")
    public ResponseEntity<Void> remover(@PathVariable Long medicamentoId){
        service.delete(medicamentoId);
        return ResponseEntity.noContent().build();
    }
}
