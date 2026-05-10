package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.dto.consulta.ConsultaAgendaItemDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDetailsDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaFilterDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaMetadataDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaMinDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaTableItemDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaUpdateDTO;
import com.inflowia.medicflow.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(ApiPaths.CONSULTAS)
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('consultas:write')")
    public ResponseEntity<ConsultaDetailsDTO> criar(
            @RequestBody @Valid ConsultaDTO dto,
            UriComponentsBuilder uriBuilder) {

        ConsultaDetailsDTO created = service.criar(dto);
        var uri = uriBuilder.path(ApiPaths.CONSULTAS + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('consultas:write')")
    public ResponseEntity<ConsultaDetailsDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ConsultaUpdateDTO dto) {

        ConsultaDetailsDTO updated = service.atualizar(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAuthority('consultas:write')")
    public ResponseEntity<ConsultaDetailsDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmar(id));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('consultas:write')")
    public ResponseEntity<ConsultaDetailsDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PatchMapping("/{id}/iniciar-atendimento")
    @PreAuthorize("hasAuthority('consultas:write')")
    public ResponseEntity<ConsultaDetailsDTO> iniciarAtendimento(@PathVariable Long id) {
        return ResponseEntity.ok(service.iniciarAtendimento(id));
    }

    @PatchMapping("/{id}/finalizar-atendimento")
    @PreAuthorize("hasAuthority('consultas:write')")
    public ResponseEntity<ConsultaDetailsDTO> finalizarAtendimento(@PathVariable Long id) {
        return ResponseEntity.ok(service.finalizarAtendimento(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('consultas:delete')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/metadata")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<ConsultaMetadataDTO> metadata() {
        return ResponseEntity.ok(service.listarMetadata());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<ConsultaDetailsDTO> buscarPorId(@PathVariable Long id) {
        ConsultaDetailsDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaMinDTO>> listar(
            ConsultaFilterDTO filtro,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        Page<ConsultaMinDTO> page = service.listar(filtro, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/agenda")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaAgendaItemDTO>> listarAgenda(
            ConsultaFilterDTO filtro,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        return ResponseEntity.ok(service.listarParaAgenda(filtro, pageable));
    }

    @GetMapping("/tabela")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaTableItemDTO>> listarTabela(
            ConsultaFilterDTO filtro,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        return ResponseEntity.ok(service.listarParaTabela(filtro, pageable));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaMinDTO>> listarPorPaciente(
            @PathVariable Long pacienteId,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {

        Page<ConsultaMinDTO> page = service.listarPorPaciente(pacienteId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/paciente/{pacienteId}/ultima")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<ConsultaDetailsDTO> buscarUltimaConsultaPorPaciente(
            @PathVariable Long pacienteId) {

        ConsultaDetailsDTO dto = service.buscarUltimaConsultaPorPaciente(pacienteId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/medico/{medicoId}")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaMinDTO>> listarPorMedico(
            @PathVariable Long medicoId,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {

        Page<ConsultaMinDTO> page = service.listarPorMedico(medicoId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaMinDTO>> listarPorStatus(
            @RequestParam StatusConsulta status,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        Page<ConsultaMinDTO> page = service.listarPorStatus(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAuthority('consultas:read')")
    public ResponseEntity<Page<ConsultaMinDTO>> listarPorPeriodo(
            ConsultaFilterDTO filtro,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {

        Page<ConsultaMinDTO> page = service.listarPorPeriodo(filtro, pageable);
        return ResponseEntity.ok(page);
    }
}
