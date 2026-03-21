package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLoginIgnoreCase(String login);

    Optional<Usuario> findByLoginIgnoreCaseAndAtivoTrue(String login);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByEmailIgnoreCaseAndAtivoTrue(String email);

    boolean existsByLoginIgnoreCase(String login);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("""
            select distinct u from Usuario u
            left join u.roles r
            where lower(u.nome) like lower(concat('%', :nome, '%'))
              and (:ativo is null or u.ativo = :ativo)
              and (:roleAuthority is null or r.authority = :roleAuthority)
            """)
    Page<Usuario> searchForAdmin(@Param("nome") String nome,
                                       @Param("ativo") Boolean ativo,
                                       @Param("roleAuthority") String roleAuthority,
                                       Pageable pageable);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByCpfAndAtivoTrue(String cpf);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);

    boolean existsByIdAndAtivoTrue(Long id);
}
