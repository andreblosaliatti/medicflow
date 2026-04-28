package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.usuario.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Optional<Medico> findByLoginIgnoreCaseAndAtivoTrue(String login);

    List<Medico> findAllByAtivoTrue();

    @Query("""
            select m from Medico m
            where m.ativo = true
              and (
                    :termo is null
                    or trim(:termo) = ''
                    or lower(m.nome) like lower(concat('%', :termo, '%'))
                    or lower(m.sobrenome) like lower(concat('%', :termo, '%'))
                    or lower(m.crm) like lower(concat('%', :termo, '%'))
                    or lower(m.especialidade) like lower(concat('%', :termo, '%'))
              )
            order by m.nome asc, m.sobrenome asc
            """)
    List<Medico> searchActiveForSelect(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.consultas c LEFT JOIN FETCH c.paciente WHERE m.ativo = true")
    List<Medico> findAllActiveWithConsultasAndPacientes();
}
