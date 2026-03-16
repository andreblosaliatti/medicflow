package com.inflowia.medicflow.entities.medicamento;

import com.inflowia.medicflow.entities.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String dosagem;

    @Column(nullable = false)
    private String frequencia;

    @Column(nullable = false)
    private String via;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_base_id")
    private MedicamentoBase medicamentoBase;
}