package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.medicamento.MedicamentoBaseDTO;
import com.inflowia.medicflow.service.MedicamentoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/medicamentos-base")
public class MedicamentoBaseController {

    @Autowired
    private MedicamentoBaseService service;

    @GetMapping
    @PreAuthorize("hasAuthority('medicamentos-base:read')")
    public List<MedicamentoBaseDTO> buscaPorDCB(@RequestParam String q){
        return service.buscaPorDCB(q);
    }

}
