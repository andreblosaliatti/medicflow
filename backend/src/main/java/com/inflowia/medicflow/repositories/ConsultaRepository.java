package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Page<Consulta> findByPacienteId(Long pacienteId, Pageable pageable);

    Page<Consulta> findByMedicoId(Long medicoId, Pageable pageable);

    List<Consulta> findByStatus(StatusConsulta status);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio,  LocalDateTime fim);

    Optional<Consulta> findTopByPacienteIdOrderByDataHoraDesc(Long pacienteId);

    @Query("SELECT DISTINCT c.paciente FROM Consulta c WHERE c.medico.id = :medicoId")
    List<Paciente> findPacientesDistinctByMedicoId(@Param("medicoId") Long medicoId);

    Optional<Consulta> findTopByPacienteIdOrderByIdDesc(Long pacienteId);

}
