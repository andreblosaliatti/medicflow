package com.inflowia.medicflow.entities.exame;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exame_base")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExameBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String codigoTuss;

    @Enumerated(EnumType.STRING)
    private TipoExame tipo;
    private int prazoEstimado;

    @OneToMany(mappedBy = "exameBase")
    private List<ExameSolicitado> exameSolicitado = new ArrayList<>();
}
