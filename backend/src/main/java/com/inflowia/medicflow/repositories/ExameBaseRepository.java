package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.exame.ExameBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExameBaseRepository extends JpaRepository<ExameBase, Long> {

    Page<ExameBase> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}