package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPaciente(Paciente paciente);

    List<Consulta> findByMedico(Medico medico);

    List<Consulta> findByStatus(StatusConsulta status);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio,  LocalDateTime fim);

    Consulta findTop1ByPacienteIdOrderByDataHoraDesc(Long pacienteId);

}
