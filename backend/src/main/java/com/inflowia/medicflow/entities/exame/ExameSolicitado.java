package com.inflowia.medicflow.entities.exame;

import com.inflowia.medicflow.entities.consulta.Consulta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "exame_solicitado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExameSolicitado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusExame status;
    private String justificativa;
    private String observacoes;
    private LocalDateTime dataColeta;
    private LocalDateTime dataResultado;

    @ManyToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @ManyToOne
    @JoinColumn(name = "exame_base_id")
    private ExameBase exameBase;
}
