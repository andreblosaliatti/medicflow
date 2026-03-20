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

    @Column(name = "nome_prescrito", nullable = false)
    private String nome;

    @Column(name = "dosagem_prescrita", nullable = false)
    private String dosagem;

    @Column(name = "frequencia")
    private String frequencia;

    @Column(name = "via")
    private String via;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicamento_base_id", nullable = false)
    private MedicamentoBase medicamentoBase;
}