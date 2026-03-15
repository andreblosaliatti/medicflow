package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthorityIgnoreCase(String authority);
}
