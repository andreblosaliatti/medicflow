package com.inflowia.medicflow.domain.usuario;

import com.inflowia.medicflow.domain.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Medico extends Usuario {

    @Column(nullable = false, unique = true, length = 50)
    private String crm;

    @Column(length = 120)
    private String especialidade;

    @Column(name = "instituicao_formacao", length = 255)
    private String instituicaoFormacao;

    @Column(name = "data_formacao")
    private LocalDate dataFormacao;

    @Column(length = 20)
    private String sexo;

    @Column(length = 1000)
    private String observacoes;

    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas = new ArrayList<>();
}