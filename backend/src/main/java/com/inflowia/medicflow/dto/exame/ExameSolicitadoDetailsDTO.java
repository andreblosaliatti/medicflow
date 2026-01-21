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
public class ExameSolicitadoDetailsDTO {

    private Long id;
    private StatusExame status;
    private String justificativa;
    private String observacoes;
    private LocalDateTime dataColeta;
    private LocalDateTime dataResultado;

    private Long consultaId;
    private Long pacienteId;
    private String pacientePrimeiroNome;
    private String pacienteSobrenome;

    private Long exameBaseId;
    private String exameNome;
    private String codigoTuss;

    public ExameSolicitadoDetailsDTO(ExameSolicitado entity) {
        this.id = entity.getId().longValue();
        this.status = entity.getStatus();
        this.justificativa = entity.getJustificativa();
        this.observacoes = entity.getObservacoes();
        this.dataColeta = entity.getDataColeta();
        this.dataResultado = entity.getDataResultado();

        if (entity.getConsulta() != null) {
            this.consultaId = entity.getConsulta().getId();
            if (entity.getConsulta().getPaciente() != null) {
                this.pacienteId = entity.getConsulta().getPaciente().getId();
                this.pacientePrimeiroNome = entity.getConsulta().getPaciente().getPrimeiroNome();
                this.pacientePrimeiroNome = entity.getConsulta().getPaciente().getSobrenome();
            }
        }

        if (entity.getExameBase() != null) {
            this.exameBaseId = entity.getExameBase().getId();
            this.exameNome = entity.getExameBase().getNome();
            this.codigoTuss = entity.getExameBase().getCodigoTuss();
        }
    }
}
