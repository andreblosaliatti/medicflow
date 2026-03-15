package com.inflowia.medicflow.dto.exame;

import com.inflowia.medicflow.entities.exame.StatusExame;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExameSolicitadoUpdateDTO {
    @NotNull
    private Long consultaId;

    @NotNull
    private Long exameBaseId;

    private StatusExame status;
    private String justificativa;
    private String observacoes;
    private LocalDateTime dataColeta;
    private LocalDateTime dataResultado;
}
