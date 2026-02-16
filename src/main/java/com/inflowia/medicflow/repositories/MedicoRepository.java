package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByCrm(String crm);

    boolean existsByCrm(String crm);

    List<Medico> findByEspecialidadeIgnoreCaseContaining(String especialidade);

    Optional<Medico> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Page<Medico> findByAtivoTrue(Pageable pageable);

    Page<Medico> findByAtivoFalse(Pageable pageable);
}
