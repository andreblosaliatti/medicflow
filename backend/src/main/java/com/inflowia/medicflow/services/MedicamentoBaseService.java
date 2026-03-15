package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medicamento.MedicamentoBaseDTO;
import com.inflowia.medicflow.repositories.MedicamentoBaseRepository;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoBaseService {

    @Autowired
    private MedicamentoBaseRepository repository;

    public List<MedicamentoBaseDTO> buscaPorDCB(String q) {
        List<MedicamentoBaseDTO> medicamentos = repository.findByDcbContainingIgnoreCase(q)
                .stream()
                .map(MedicamentoBaseDTO::new)
                .toList();

        if (medicamentos.isEmpty()) {
            throw new ResourceNotFoundException("Medicamento base não encontrado");
        }

        return medicamentos;
    }
}
