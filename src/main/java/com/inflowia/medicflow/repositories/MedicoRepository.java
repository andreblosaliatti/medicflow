package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    // ----- Campos ESPECÍFICOS de Medico -----
    Optional<Medico> findByCrm(String crm);
    boolean existsByCrm(String crm);

    List<Medico> findByEspecialidadeIgnoreCaseContaining(String especialidade);

    // ----- Campos HERDADOS de Usuario -----
    Optional<Medico> findByEmail(String email);
    Optional<Medico> findByLogin(String login);
    Optional<Medico> findByCpf(String cpf);

    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByCpf(String cpf);

    // Listagens úteis
    Page<Medico> findByAtivoTrue(Pageable pageable);
    Page<Medico> findByAtivoFalse(Pageable pageable);
}
