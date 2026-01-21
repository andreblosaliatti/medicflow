package com.inflowia.medicflow.dto.exame;

import com.inflowia.medicflow.entities.exame.ExameSolicitado;
import com.inflowia.medicflow.entities.exame.StatusExame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExameSolicitadoMinDTO {

    private Long id;
    private StatusExame status;
    private String justificativa;
    private String observacoes;
    private LocalDateTime dataColeta;
    private LocalDateTime dataResultado;
    private Long consultaId;
    private Long exameBaseId;
    private String exameNome;

    public ExameSolicitadoMinDTO(ExameSolicitado entity) {
        this.id = entity.getId().longValue();
        this.status = entity.getStatus();
        this.justificativa = entity.getJustificativa();
        this.observacoes = entity.getObservacoes();
        this.dataColeta = entity.getDataColeta();
        this.dataResultado = entity.getDataResultado();
        this.consultaId = entity.getConsulta() != null ? entity.getConsulta().getId() : null;
        this.exameBaseId = entity.getExameBase() != null ? entity.getExameBase().getId() : null;
        this.exameNome = entity.getExameBase() != null ? entity.getExameBase().getNome() : null;
    }
}
