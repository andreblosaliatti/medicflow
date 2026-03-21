package com.inflowia.medicflow.dto.exame;

import com.inflowia.medicflow.domain.exame.StatusExame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExameSolicitadoPatchDTO {

    private StatusExame status;
    private LocalDateTime dataColeta;
    private LocalDateTime dataResultado;
    private String observacoes;
}
