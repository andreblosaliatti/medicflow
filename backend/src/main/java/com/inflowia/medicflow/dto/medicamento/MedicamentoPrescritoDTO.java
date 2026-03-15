package com.inflowia.medicflow.dto.medicamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoPrescritoDTO {

    @NotNull(message = "Campo obrigatório")
    @Positive(message = "O medicamento base id deve ser um inteiro positivo")
     Long medicamentoBaseId;

    @NotBlank(message = "Campo obrigatório")
     String nome;

    @NotBlank(message = "Campo obrigatório")
     String dosagem;

    @NotBlank(message = "Campo obrigatório")
     String frequencia;

    @NotBlank(message = "Campo obrigatório")
     String via;
}
