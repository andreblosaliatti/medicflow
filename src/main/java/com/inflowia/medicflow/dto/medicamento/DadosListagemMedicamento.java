package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
    
public record DadosListagemMedicamento (
    Long id,
    String nome,
    String doasgem,
    String frequencia,
    String via
){
    public DadosListagemMedicamento(MedicamentoPrescrito m) {
        this(m.getId(), m.getNome(), m.getDosagem(), m.getFrequencia(), m.getVia());
    }
}
