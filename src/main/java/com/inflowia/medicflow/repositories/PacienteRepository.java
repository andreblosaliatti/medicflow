package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.paciente.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long>{

    Page<Paciente> findAllByAtivoTrue(Pageable pageable);


    Page<Paciente> findAllByAtivoFalse(Pageable pageable);
}


