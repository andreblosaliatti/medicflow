package com.inflowia.medicflow.service;

import com.inflowia.medicflow.dto.medicamento.MedicamentoBaseDTO;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.repository.MedicamentoBaseRepository;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
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
            throw new ResourceNotFoundException(ErrorCodes.MEDICAMENTO_BASE_NOT_FOUND, ExceptionMessages.notFound("Medicamento base"));
        }

        return medicamentos;
    }
}
