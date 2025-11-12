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
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemMedicamento>> findAll() {
        List<DadosListagemMedicamento> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DadosListagemMedicamento>> findByPacienteId(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.findByPaciente(pacienteId));
    }

    @PostMapping("/consultas/{id}")
    public ResponseEntity<Void> adicionar(@PathVariable Long id, @RequestBody DadosCadastroMedicamento dados){
        service.adicionarMedicamento(id, dados);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/consultas/{id}")
    public ResponseEntity<List<DadosListagemMedicamento>> listar(@PathVariable Long id){
        return ResponseEntity.ok(service.listarMedicamentos(id));
    }

    @GetMapping(value = "/{medicamentoId}")
    public ResponseEntity<DadosListagemMedicamento> findById(@PathVariable Long medicamentoId){
        DadosListagemMedicamento dto = service.findById(medicamentoId);
        return ResponseEntity.ok().body(dto);
    }

    
    @DeleteMapping("/{medicamentoId}")
    public ResponseEntity<Void> remover(@PathVariable Long medicamentoId){
        service.delete(medicamentoId);
        return ResponseEntity.noContent().build();
    }


}
