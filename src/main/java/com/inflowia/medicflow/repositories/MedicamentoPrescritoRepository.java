package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicamentoPrescritoRepository extends JpaRepository<MedicamentoPrescrito, Long> {

    Page<MedicamentoPrescrito> findByConsultaPacienteId(Long pacienteId, Pageable pageable);

    @Query("SELECT obj FROM MedicamentoPrescrito obj " +
            "WHERE UPPER(obj.nome) LIKE UPPER(CONCAT('%', :nome, '%'))")
    Page<MedicamentoPrescrito> searchByName( String nome, Pageable pageable);}
