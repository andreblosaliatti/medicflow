package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.medicamento.DadosCadastroMedicamento;
import com.inflowia.medicflow.dto.medicamento.DadosListagemMedicamento;
import com.inflowia.medicflow.services.MedicamentoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/pacientes/{id}/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> adicionar(@PathVariable Long id, @RequestBody DadosCadastroMedicamento dados){
        service.adicionarMedicamento(id, dados);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemMedicamento>> listar(@PathVariable Long id){
        return ResponseEntity.ok(service.listarMedicamentos(id));
    }
    
    @DeleteMapping("/{medicamentoiId}")
    public ResponseEntity<Void> remover(@PathVariable Long medicamentoId){
        service.removerMedicamento(medicamentoId);
        return ResponseEntity.noContent().build();
    }

}
