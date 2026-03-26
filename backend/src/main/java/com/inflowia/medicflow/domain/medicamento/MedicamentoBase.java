package com.inflowia.medicflow.domain.medicamento;

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

    @Column(length = 255)
    private String dcb;

    @Column(name = "forma_farmaceutica", length = 120)
    private String formaFarmaceutica;

    @Column(name = "dosagem_padrao", length = 120)
    private String dosagemPadrao;

    @Column(length = 120)
    private String concentracao;

    @Column(name = "via_administracao", length = 100)
    private String viaAdministracao;

    @Column(name = "codigo_atc", length = 50)
    private String codigoATC;

    @Column(name = "registro_anvisa", length = 50)
    private String registroAnvisa;

    @Column(length = 255)
    private String fabricante;

    @Column(nullable = false)
    @Builder.Default
    private Boolean controlado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Builder.Default
    @OneToMany(mappedBy = "medicamentoBase")
    private List<MedicamentoPrescrito> medicamentosPrescritos = new ArrayList<>();
}