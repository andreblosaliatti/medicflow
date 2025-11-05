package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.medicamento.MedicamentoBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentoBaseRespository extends JpaRepository<MedicamentoBase, Long> {
    List<MedicamentoBase> findByDcbContainingIgnoreCase(String dcb);

}
