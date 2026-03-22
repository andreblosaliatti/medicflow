package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.medico.MedicoComPacientesDTO;
import com.inflowia.medicflow.dto.medico.MedicoDTO;
import com.inflowia.medicflow.dto.medico.MedicoDetailsDTO;
import com.inflowia.medicflow.dto.medico.MedicoMinDTO;
import com.inflowia.medicflow.dto.medico.MedicoSelectDTO;
import com.inflowia.medicflow.dto.medico.MedicoUpdateDTO;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService service;

    @PostMapping
    @PreAuthorize("hasAuthority('medicos:write')")
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
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<Page<MedicoMinDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<MedicoMinDTO> page = service.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<List<MedicoSelectDTO>> listarResumo(
            @RequestParam(value = "termo", required = false) String termo,
            @RequestParam(value = "limite", defaultValue = "20") int limite) {
        return ResponseEntity.ok(service.listarResumo(termo, limite));
    }

    @GetMapping("/autocomplete")
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<List<MedicoSelectDTO>> autocomplete(
            @RequestParam("termo") String termo,
            @RequestParam(value = "limite", defaultValue = "10") int limite) {
        return ResponseEntity.ok(service.listarResumo(termo, limite));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<MedicoDetailsDTO> buscarPorId(@PathVariable Long id) {
        MedicoDetailsDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/com-pacientes")
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<MedicoComPacientesDTO> buscarMedicoComPacientes(@PathVariable Long id) {
        MedicoComPacientesDTO dto = service.buscarMedicoComPacientes(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/pacientes")
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<List<PacienteMinDTO>> listarPacientesPorMedico(@PathVariable Long id) {
        List<PacienteMinDTO> pacientes = service.listarPacientesPorMedico(id);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/com-pacientes")
    @PreAuthorize("hasAuthority('medicos:read')")
    public ResponseEntity<List<MedicoComPacientesDTO>> listarMedicosComPacientes() {
        List<MedicoComPacientesDTO> medicos = service.listarTodosMedicosComPacientes();
        return ResponseEntity.ok(medicos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('medicos:write')")
    public ResponseEntity<MedicoDetailsDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicoUpdateDTO dto) {
        MedicoDetailsDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('medicos:write')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
