package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

    Page<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByCpfAndAtivoTrue(String cpf);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);

    boolean existsByIdAndAtivoTrue(Long id);
}
