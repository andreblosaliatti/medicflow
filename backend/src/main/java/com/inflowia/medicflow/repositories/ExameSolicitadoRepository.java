package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.exame.ExameSolicitado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExameSolicitadoRepository extends JpaRepository<ExameSolicitado, Long> {

    Page<ExameSolicitado> findByConsultaId(Long consultaId, Pageable pageable);

    Page<ExameSolicitado> findByExameBaseId(Long exameBaseId, Pageable pageable);

    Page<ExameSolicitado> findByConsultaPacienteId(Long pacienteId, Pageable pageable);
}
