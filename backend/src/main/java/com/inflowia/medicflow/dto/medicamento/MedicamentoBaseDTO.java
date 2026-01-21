package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.entities.medicamento.MedicamentoBase;

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
        principioAtivo = m.getPricipioAtivo();
        formaFarmaceutica = m.getFormaFarmaceutica();
        dosagemPadrao = m.getDosagemPadrão();
        viaAdministracao = m.getViaAdministracao();
    }
}
