package com.inflowia.medicflow.entities.medicamento;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicamento_base")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentoBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicamento_base_seq_gen")
    @SequenceGenerator(
            name = "medicamento_base_seq_gen",
            sequenceName = "medicamento_base_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "nome_comercial", length = 255)
    private String nomeComercial;

    @Column(name = "principio_ativo", length = 255)
    private String principioAtivo;

    @Column(name = "dcb", length = 255)
    private String dcb;

    @Column(name = "forma_farmaceutica", length = 120)
    private String formaFarmaceutica;

    @Column(name = "dosagem_padrao", length = 120)
    private String dosagemPadrao;

    @Column(name = "concentracao", length = 120)
    private String concentracao;

    @Column(name = "via_administracao", length = 100)
    private String viaAdministracao;

    @Column(name = "codigo_atc", length = 50)
    private String codigoATC;

    @Column(name = "registro_anvisa", length = 50)
    private String registroAnvisa;

    @Column(name = "fabricante", length = 255)
    private String fabricante;

    @Column(name = "controlado", nullable = false)
    @Builder.Default
    private Boolean controlado = false;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @OneToMany(mappedBy = "medicamentoBase")
    @Builder.Default
    private List<MedicamentoPrescrito> medicamentoPrescrito = new ArrayList<>();
}