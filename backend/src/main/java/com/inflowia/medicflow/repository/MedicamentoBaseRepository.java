package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentoBaseRepository extends JpaRepository<MedicamentoBase, Long> {
    List<MedicamentoBase> findByDcbContainingIgnoreCase(String dcb);
}
