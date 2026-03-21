package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long>, JpaSpecificationExecutor<Consulta> {

    Optional<Consulta> findTopByPacienteIdOrderByDataHoraDesc(Long pacienteId);

    @Query("SELECT DISTINCT c.paciente FROM Consulta c WHERE c.medico.id = :medicoId")
    List<Paciente> findPacientesDistinctByMedicoId(@Param("medicoId") Long medicoId);

    Optional<Consulta> findTopByPacienteIdOrderByIdDesc(Long pacienteId);

    long countByPacienteId(Long pacienteId);
}
