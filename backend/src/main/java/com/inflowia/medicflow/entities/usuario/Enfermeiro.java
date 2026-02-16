package com.inflowia.medicflow.entities.usuario;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "enfermeiros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enfermeiro extends Usuario {

    @Column(nullable = false, unique = true)
    private String coren;

    @Column(length = 2, nullable = false)
    private String ufCoren; // RS, SP, etc.

    @Column(length = 120)
    private String especialidadeEnfermagem; // Ex: UTI, Pediatria, etc.

    @Enumerated(EnumType.STRING)
    private SetorClinico setorClinico;

    @Enumerated(EnumType.STRING)
    private TurnoTrabalho turnoTrabalho;

    private LocalDate dataAdmissao;

    private LocalDate dataDemissao;

    @Column(length = 1000)
    private String observacoes;


}
