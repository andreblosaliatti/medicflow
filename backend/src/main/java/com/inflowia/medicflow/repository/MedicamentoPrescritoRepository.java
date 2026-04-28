package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicamentoPrescritoRepository extends JpaRepository<MedicamentoPrescrito, Long> {

    long countByConsultaPacienteId(Long pacienteId);

    Page<MedicamentoPrescrito> findByConsultaPacienteId(Long pacienteId, Pageable pageable);

    Page<MedicamentoPrescrito> findByConsultaPacienteIdAndConsultaMedicoId(Long pacienteId, Long medicoId, Pageable pageable);

    List<MedicamentoPrescrito> findByConsultaPacienteId(Long pacienteId);

    Page<MedicamentoPrescrito> findByConsultaId(Long consultaId, Pageable pageable);

    Page<MedicamentoPrescrito> findByConsultaMedicoId(Long medicoId, Pageable pageable);

    @Query("""
            SELECT obj
            FROM MedicamentoPrescrito obj
            WHERE UPPER(obj.nome) LIKE UPPER(CONCAT('%', :nome, '%'))
            """)
    Page<MedicamentoPrescrito> searchByName(@Param("nome") String nome, Pageable pageable);

    @Query("""
            SELECT obj
            FROM MedicamentoPrescrito obj
            WHERE UPPER(obj.nome) LIKE UPPER(CONCAT('%', :nome, '%'))
              AND obj.consulta.medico.id = :medicoId
            """)
    Page<MedicamentoPrescrito> searchByNameAndMedicoId(@Param("nome") String nome,
                                                       @Param("medicoId") Long medicoId,
                                                       Pageable pageable);
}
