package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.paciente.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findAllByAtivoTrue(Pageable pageable);

    Page<Paciente> findAllByAtivoFalse(Pageable pageable);

    Optional<Paciente> findByIdAndAtivoTrue(Long id);

    boolean existsByIdAndAtivoTrue(Long id);
}
