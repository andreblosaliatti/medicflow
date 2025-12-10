package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.medico.*;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.services.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService service;

    // POST - cadastrar médico
    @PostMapping
    public ResponseEntity<MedicoDetailsDTO> cadastrar(
            @RequestBody @Valid MedicoDTO dto,
            UriComponentsBuilder uriBuilder) {

        MedicoDetailsDTO salvo = service.cadastrar(dto);

        URI uri = uriBuilder
                .path("/medicos/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(uri).body(salvo);
    }

    @GetMapping
    public ResponseEntity<Page<MedicoMinDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<MedicoMinDTO> page = service.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoDetailsDTO> buscarPorId(@PathVariable Long id) {
        MedicoDetailsDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/com-pacientes")
    public ResponseEntity<MedicoComPacientesDTO> buscarMedicoComPacientes(@PathVariable Long id) {
        MedicoComPacientesDTO dto = service.buscarMedicoComPacientes(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/pacientes")
    public ResponseEntity<List<PacienteMinDTO>> listarPacientesPorMedico(@PathVariable Long id) {
        List<PacienteMinDTO> pacientes = service.listarPacientesPorMedico(id);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/com-pacientes")
    public ResponseEntity<List<MedicoComPacientesDTO>> listarMedicosComPacientes() {
        List<MedicoComPacientesDTO> medicos = service.listarTodosMedicosComPacientes();
        return ResponseEntity.ok(medicos);
    }

    // PUT - atualizar
    @PutMapping("/{id}")
    public ResponseEntity<MedicoDetailsDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicoUpdateDTO dto) {
        MedicoDetailsDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    // DELETE - hard delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


}
