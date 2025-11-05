package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MedicamentoPrescritoRepository extends JpaRepository<MedicamentoPrescrito, Long> {

}
