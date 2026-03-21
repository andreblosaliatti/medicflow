package com.inflowia.medicflow.dto.medicamento;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoPrescritoDTO {

    @Positive(message = "O medicamento base id deve ser um inteiro positivo")
    private Long medicamentoBaseId;

    private String nome;

    @NotBlank(message = "Campo obrigatório")
    private String dosagem;

    @NotBlank(message = "Campo obrigatório")
    private String frequencia;

    @NotBlank(message = "Campo obrigatório")
    private String via;

    @AssertTrue(message = "Informe o nome do medicamento livre ou selecione um medicamento base.")
    public boolean isModoDePrescricaoInformado() {
        return medicamentoBaseId != null || (nome != null && !nome.isBlank());
    }

    @AssertTrue(message = "Informe apenas um medicamento: nome livre ou medicamento base.")
    public boolean isModoDePrescricaoValido() {
        return medicamentoBaseId == null || nome == null || nome.isBlank();
    }
}
