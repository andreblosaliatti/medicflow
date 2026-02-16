package com.inflowia.medicflow.entities.medicamento;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentoBase {

    @Id
    @GeneratedValue
    private Long id;

    private String dcb;
    private String nomeComercial;
    private String pricipioAtivo;
    private String formaFarmaceutica;
    private String dosagemPadrão;
    private String viaAdministracao;
    private String codigoATC;

    @OneToMany(mappedBy = "medicamentoBase")
    private List<MedicamentoPrescrito> medicamentoPrescrito = new ArrayList<>();

}
