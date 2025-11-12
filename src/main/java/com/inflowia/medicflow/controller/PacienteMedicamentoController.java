package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.medicamento.DadosListagemMedicamento;
import com.inflowia.medicflow.services.MedicamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes/{pacienteId}/medicamentos")
public class PacienteMedicamentoController {

    private final MedicamentoService service;

    public PacienteMedicamentoController(MedicamentoService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemMedicamento>> listarTodos(@PathVariable Long pacienteId){
        return ResponseEntity.ok(service.findByPaciente(pacienteId));
    }

    @GetMapping("/atual")
    public ResponseEntity<List<DadosListagemMedicamento>> listarAtual(@PathVariable Long pacienteId){
        return ResponseEntity.ok(service.listarMedicacaoAtual(pacienteId));
    }
}