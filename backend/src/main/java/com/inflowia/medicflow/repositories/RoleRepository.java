package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.usuario.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
