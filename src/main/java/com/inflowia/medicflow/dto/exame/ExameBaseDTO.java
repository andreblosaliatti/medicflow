package com.inflowia.medicflow.dto.exame;

import com.inflowia.medicflow.entities.exame.TipoExame;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExameBaseDTO {

    @NotBlank
    private String nome;

    private String codigoTuss;

    @NotNull
    private TipoExame tipo;

    private int prazoEstimado;
}
