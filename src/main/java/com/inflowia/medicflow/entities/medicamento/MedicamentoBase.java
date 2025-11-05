package com.inflowia.medicflow.entities.medicamento;

import jakarta.persistence.*;
import lombok.*;

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

    private String dcb;                   //Denominação comum brasileira
    private String nomeComercial;         //Ex: Tylenol
    private String pricipioAtivo;         //Ex: Paracetamol
    private String formaFarmaceutica;     //Ex: Comprimidos
    private String dosagemPadrão;         //Ex: 500mg
    private String viaAdministracao;      //Ex: Oral
    private String codigoATC;             //Classficação OMS

}
