package com.inflowia.medicflow.domain.exame;

import com.inflowia.medicflow.domain.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exame_solicitado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExameSolicitado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusExame status;

    @Column(length = 1000)
    private String justificativa;

    @Column(length = 1000)
    private String observacoes;

    @Column(name = "data_coleta")
    private LocalDateTime dataColeta;

    @Column(name = "data_resultado")
    private LocalDateTime dataResultado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exame_base_id", nullable = false)
    private ExameBase exameBase;
}