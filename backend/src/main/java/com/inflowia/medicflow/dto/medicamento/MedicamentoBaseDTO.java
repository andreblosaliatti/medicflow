package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import lombok.Data;

@Data
public class MedicamentoBaseDTO {

    Long id;
    String dcb;
    String nomeComercial;
    String principioAtivo;
    String formaFarmaceutica;
    String dosagemPadrao;
    String viaAdministracao;

    public MedicamentoBaseDTO(MedicamentoBase m){
        id =  m.getId();
        dcb = m.getDcb();
        nomeComercial = m.getNomeComercial();
        principioAtivo = m.getPrincipioAtivo();
        formaFarmaceutica = m.getFormaFarmaceutica();
        dosagemPadrao = m.getDosagemPadrao();
        viaAdministracao = m.getViaAdministracao();
    }
}
