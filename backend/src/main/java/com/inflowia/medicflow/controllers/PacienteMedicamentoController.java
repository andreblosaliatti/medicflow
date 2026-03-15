package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import com.inflowia.medicflow.services.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes/{pacienteId}/medicamentos")
public class PacienteMedicamentoController {

    @Autowired
    private MedicamentoService service;

    @GetMapping
    public ResponseEntity<Page<MedicamentoPrescritoMinDTO>> listarTodos(@PathVariable Long pacienteId, Pageable pageable){
        return ResponseEntity.ok(service.findByPaciente(pacienteId, pageable));
    }

    @GetMapping("/atual")
    public ResponseEntity<List<MedicamentoPrescritoMinDTO>> listarAtual(@PathVariable Long pacienteId){
        return ResponseEntity.ok(service.listarMedicacaoAtual(pacienteId));
    }
}