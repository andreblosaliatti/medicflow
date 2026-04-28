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

    Optional<Consulta> findTopByPacienteIdAndMedicoIdOrderByDataHoraDesc(Long pacienteId, Long medicoId);

    Optional<Consulta> findTopByPacienteIdOrderByIdDesc(Long pacienteId);

    Optional<Consulta> findTopByPacienteIdAndMedicoIdOrderByIdDesc(Long pacienteId, Long medicoId);

    List<Consulta> findByPacienteIdOrderByDataHoraDesc(Long pacienteId);

    List<Consulta> findByPacienteIdAndMedicoIdOrderByDataHoraDesc(Long pacienteId, Long medicoId);

    long countByPacienteId(Long pacienteId);

    long countByPacienteIdAndMedicoId(Long pacienteId, Long medicoId);

    boolean existsByPacienteIdAndMedicoId(Long pacienteId, Long medicoId);

    @Query("SELECT DISTINCT c.paciente FROM Consulta c WHERE c.medico.id = :medicoId")
    List<Paciente> findPacientesDistinctByMedicoId(@Param("medicoId") Long medicoId);

    // 🔥 NOVO MÉTODO (ESSENCIAL para resolver N+1)
    @Query("""
        SELECT c
        FROM Consulta c
        WHERE c.paciente.id IN :pacienteIds
        ORDER BY c.paciente.id ASC, c.dataHora DESC
    """)
    List<Consulta> buscarConsultasOrdenadasPorPacienteEDataDesc(
            @Param("pacienteIds") List<Long> pacienteIds
    );

    @Query("""
        SELECT c
        FROM Consulta c
        WHERE c.paciente.id IN :pacienteIds
          AND c.medico.id = :medicoId
        ORDER BY c.paciente.id ASC, c.dataHora DESC
    """)
    List<Consulta> buscarConsultasOrdenadasPorPacienteEDataDescPorMedico(
            @Param("pacienteIds") List<Long> pacienteIds,
            @Param("medicoId") Long medicoId
    );
}
