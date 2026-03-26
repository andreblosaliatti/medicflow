package com.inflowia.medicflow.domain.usuario;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "enfermeiros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Enfermeiro extends Usuario {

    @Column(nullable = false, unique = true, length = 50)
    private String coren;

    @Column(name = "uf_coren", nullable = false, length = 2)
    private String ufCoren;

    @Column(name = "especialidade_enfermagem", length = 120)
    private String especialidadeEnfermagem;

    @Enumerated(EnumType.STRING)
    @Column(name = "setor_clinico", length = 50)
    private SetorClinico setorClinico;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno_trabalho", length = 30)
    private TurnoTrabalho turnoTrabalho;

    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    @Column(length = 1000)
    private String observacoes;
}