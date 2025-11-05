package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLoginIgnoreCase(String Login);
    Optional<Usuario> findByEmailIgnoreCase(String Email);

    boolean existsByLoginIgnoreCase(String login);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
