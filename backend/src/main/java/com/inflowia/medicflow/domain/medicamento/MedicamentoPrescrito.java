package com.inflowia.medicflow.domain.medicamento;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
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

    @Column(name = "nome_prescrito", nullable = false)
    private String nome;

    @Column(name = "dosagem_prescrita", nullable = false)
    private String dosagem;

    @Column(name = "frequencia")
    private String frequencia;

    @Column(name = "via")
    private String via;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_base_id")
    private MedicamentoBase medicamentoBase;
}