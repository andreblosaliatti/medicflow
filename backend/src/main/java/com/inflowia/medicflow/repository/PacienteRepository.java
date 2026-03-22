package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.paciente.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Page<Paciente> findAllByAtivoTrue(Pageable pageable);

    Page<Paciente> findAllByAtivoFalse(Pageable pageable);

    Optional<Paciente> findByIdAndAtivoTrue(Long id);

    boolean existsByIdAndAtivoTrue(Long id);

    @Query("""
            SELECT p
            FROM Paciente p
            WHERE UPPER(CONCAT(COALESCE(p.primeiroNome, ''), ' ', COALESCE(p.sobrenome, '')))
                    LIKE UPPER(CONCAT('%', COALESCE(:nome, ''), '%'))
              AND COALESCE(p.cpf, '') LIKE CONCAT('%', COALESCE(:cpf, ''), '%')
              AND (:ativo IS NULL OR p.ativo = :ativo)
              AND UPPER(COALESCE(p.planoSaude, ''))
                    LIKE UPPER(CONCAT('%', COALESCE(:convenio, ''), '%'))
            """)
    Page<Paciente> search(@Param("nome") String nome,
                          @Param("cpf") String cpf,
                          @Param("ativo") Boolean ativo,
                          @Param("convenio") String convenio,
                          Pageable pageable);
}