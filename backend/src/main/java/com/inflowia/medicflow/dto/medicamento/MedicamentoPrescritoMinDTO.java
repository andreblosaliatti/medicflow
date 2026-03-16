package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoPrescritoMinDTO {

    private Long id;
    private String nome;
    private String dosagem;
    private String frequencia;
    private String via;

    public MedicamentoPrescritoMinDTO(MedicamentoPrescrito m) {
        this(m.getId(), m.getNome(), m.getDosagem(), m.getFrequencia(), m.getVia());
    }
}