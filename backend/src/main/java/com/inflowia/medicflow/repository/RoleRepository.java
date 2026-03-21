package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.usuario.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByAuthority(String authority);

    List<Role> findByAuthorityIn(Collection<String> authorities);
}