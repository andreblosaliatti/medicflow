package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import lombok.Getter;

@Getter
public class DadosAtualizacaoMedicamento {

    private Long medicamentoBaseId;
    private String nome;
    private String dosagem;
    private String frequencia;
    private String  via;

    public DadosAtualizacaoMedicamento(MedicamentoPrescrito entity){
        medicamentoBaseId = entity.getMedicamentoBase().getId();
        nome = entity.getNome();
        dosagem = entity.getDosagem();
        frequencia = entity.getFrequencia();
        via = entity.getVia();
    }
}


