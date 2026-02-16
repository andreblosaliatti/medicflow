package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medicamento.MedicamentoBaseDTO;
import com.inflowia.medicflow.repositories.MedicamentoBaseRespository;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicamentoBaseService {

    @Autowired
    private MedicamentoBaseRespository repository;

    public List<MedicamentoBaseDTO> buscaPorDCB(String q){
        List<MedicamentoBaseDTO> medicamentos = new ArrayList<>();
        if(medicamentos.isEmpty()){
            throw new ResourceNotFoundException("Medicamneto base não encontrado");
        }

        return repository.findByDcbContainingIgnoreCase(q)
        .stream()
        .map(MedicamentoBaseDTO::new)
        .toList();
    }
}
