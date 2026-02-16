package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoPrescritoMinDTO {

    @NotBlank(message = "Campo obrigatório")
    Long id;

    @NotBlank(message = "Campo obrigatório")
    String nome;

    @NotBlank(message = "Campo obrigatório")
    String dosagem;

    @NotBlank(message = "Campo obrigatório")
    String frequencia;

    @NotBlank(message = "Campo obrigatório")
    String via;

    public MedicamentoPrescritoMinDTO(MedicamentoPrescrito m) {
        this(m.getId(), m.getNome(), m.getDosagem(), m.getFrequencia(), m.getVia());
    }
}
