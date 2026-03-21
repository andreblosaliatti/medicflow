package com.inflowia.medicflow.dto.exame;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExameSolicitadoDTO {


    @NotNull
    private Long consultaId;

    @NotNull
    private Long exameBaseId;

    private String justificativa;
    private String observacoes;
    private LocalDateTime dataColeta;
}
