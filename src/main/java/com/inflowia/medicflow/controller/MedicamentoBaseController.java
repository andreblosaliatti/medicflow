package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.medicamento.MedicamentoBaseDTO;
import com.inflowia.medicflow.services.MedicamentoBaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/medicamentos-base")
public class MedicamentoBaseController {

    private final MedicamentoBaseService service;

    public MedicamentoBaseController(MedicamentoBaseService service){
        this.service = service;
    }

    @GetMapping
    public List<MedicamentoBaseDTO> buscaPorDCB(@RequestParam String q){
        return service.buscaPorDCB(q);
    }
    
}
