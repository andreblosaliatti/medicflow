package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MedicamentoPrescritoRepository extends JpaRepository<MedicamentoPrescrito, Long> {

    List<MedicamentoPrescrito> findByConsultaPacienteId(Long pacienteId);

}
