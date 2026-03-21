package com.inflowia.medicflow.dto.exame;

import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.exame.StatusExame;
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
    private Long pacienteId;
    private Long exameBaseId;
    private String exameNome;
    private String pacienteNome;

    public ExameSolicitadoMinDTO(ExameSolicitado entity) {
        this.id = entity.getId().longValue();
        this.status = entity.getStatus();
        this.justificativa = entity.getJustificativa();
        this.observacoes = entity.getObservacoes();
        this.dataColeta = entity.getDataColeta();
        this.dataResultado = entity.getDataResultado();
        this.consultaId = entity.getConsulta() != null ? entity.getConsulta().getId() : null;
        this.pacienteId = entity.getConsulta() != null && entity.getConsulta().getPaciente() != null
                ? entity.getConsulta().getPaciente().getId()
                : null;
        this.exameBaseId = entity.getExameBase() != null ? entity.getExameBase().getId() : null;
        this.exameNome = entity.getExameBase() != null ? entity.getExameBase().getNome() : null;
        this.pacienteNome = entity.getConsulta() != null && entity.getConsulta().getPaciente() != null
                ? composeName(
                entity.getConsulta().getPaciente().getPrimeiroNome(),
                entity.getConsulta().getPaciente().getSobrenome())
                : null;
    }

    private String composeName(String first, String second) {
        StringBuilder builder = new StringBuilder();
        if (first != null && !first.isBlank()) {
            builder.append(first.trim());
        }
        if (second != null && !second.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(second.trim());
        }
        return builder.length() == 0 ? null : builder.toString();
    }
}
