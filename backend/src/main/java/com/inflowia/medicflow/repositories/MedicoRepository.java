package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByCrm(String crm);

    Optional<Medico> findByCrmAndAtivoTrue(String crm);

    boolean existsByCrm(String crm);

    List<Medico> findByEspecialidadeIgnoreCaseContaining(String especialidade);

    Optional<Medico> findByCpf(String cpf);

    Optional<Medico> findByCpfAndAtivoTrue(String cpf);

    boolean existsByCpf(String cpf);

    Page<Medico> findByAtivoTrue(Pageable pageable);

    Page<Medico> findByAtivoFalse(Pageable pageable);

    Optional<Medico> findByIdAndAtivoTrue(Long id);

    boolean existsByIdAndAtivoTrue(Long id);

    List<Medico> findAllByAtivoTrue();

    @Query("SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.consultasMedico c LEFT JOIN FETCH c.paciente WHERE m.ativo = true")
    List<Medico> findAllActiveWithConsultasAndPacientes();
}
