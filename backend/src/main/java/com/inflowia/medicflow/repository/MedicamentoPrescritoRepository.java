package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicamentoPrescritoRepository extends JpaRepository<MedicamentoPrescrito, Long> {

    long countByConsultaPacienteId(Long pacienteId);

    Page<MedicamentoPrescrito> findByConsultaPacienteId(Long pacienteId, Pageable pageable);

    Page<MedicamentoPrescrito> findByConsultaId(Long consultaId, Pageable pageable);

    @Query("""
            SELECT obj
            FROM MedicamentoPrescrito obj
            WHERE UPPER(obj.nome) LIKE UPPER(CONCAT('%', :nome, '%'))
            """)
    Page<MedicamentoPrescrito> searchByName(String nome, Pageable pageable);
}