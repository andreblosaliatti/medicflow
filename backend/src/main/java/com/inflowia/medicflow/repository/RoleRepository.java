package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.usuario.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
