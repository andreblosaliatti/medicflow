package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.consulta.ConsultaRequestDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaResponseDTO;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.services.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private  ConsultaService service;
 
@PostMapping
    public ResponseEntity<ConsultaResponseDTO> consultar(@RequestBody ConsultaRequestDTO dto){
    Consulta c = service.criar(dto);
    return ResponseEntity.ok(toResponse(c));
    }

private ConsultaResponseDTO toResponse(Consulta c){
    ConsultaResponseDTO r = new ConsultaResponseDTO();
    r.id = c.getId();
    r.dataHora = c.getDataHora();
    r.tipo = c.getTipo();
    r.status = c.getStatus();
    r.motivo = c.getMotivo();
    if (c.getPaciente() != null){
        r.pacienteId = c.getPaciente().getId();
        r.pacienteNome = c.getPaciente().getPrimeiroNome();
        r.pacienteNome = c.getPaciente().getSobrenome();
    }
    if (c.getMedico() != null){
        r.medicoId = c.getMedico().getId();
        r.medicoNome = c.getMedico().getNome();
    }
    return r;
}
}

