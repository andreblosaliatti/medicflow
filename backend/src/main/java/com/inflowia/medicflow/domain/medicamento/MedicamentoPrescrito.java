package com.inflowia.medicflow.domain.medicamento;

import com.inflowia.medicflow.domain.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "dosagem", nullable = false, length = 120)
    private String dosagem;

    @Column(length = 255)
    private String frequencia;

    @Column(length = 100)
    private String via;

    @Column(length = 1000)
    private String observacoes;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_base_id")
    private MedicamentoBase medicamentoBase;
}