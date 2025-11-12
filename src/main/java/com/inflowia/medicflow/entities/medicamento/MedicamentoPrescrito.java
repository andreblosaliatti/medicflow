package com.inflowia.medicflow.entities.medicamento;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.paciente.Paciente;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Table(name = "medicamento_prescrito")
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentoPrescrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String dosagem;
    private String frequencia;
    private String via;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @ManyToOne
    @JoinColumn(name = "medicamento_base_id")
    private MedicamentoBase medicamentoBase;

}
