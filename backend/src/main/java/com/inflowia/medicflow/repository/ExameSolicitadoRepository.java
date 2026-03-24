package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExameSolicitadoRepository extends JpaRepository<ExameSolicitado, Long> {

    long countByConsultaPacienteId(Long pacienteId);

    Page<ExameSolicitado> findByConsultaId(Long consultaId, Pageable pageable);

    Page<ExameSolicitado> findByExameBaseId(Long exameBaseId, Pageable pageable);

    Page<ExameSolicitado> findByConsultaPacienteId(Long pacienteId, Pageable pageable);

    List<ExameSolicitado> findByConsultaPacienteId(Long pacienteId);
}
