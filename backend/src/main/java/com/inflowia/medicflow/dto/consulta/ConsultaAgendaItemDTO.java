package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaAgendaItemDTO {

    private Long id;
    private LocalDateTime dataHora;
    private Integer duracaoMinutos;
    private TipoConsulta tipo;
    private StatusConsulta status;
    private String linkAcesso;
    private String pacienteNome;
    private Long medicoId;
    private String medicoNome;
    private String motivo;

    public ConsultaAgendaItemDTO(Consulta entity) {
        this.id = entity.getId();
        this.dataHora = entity.getDataHora();
        this.duracaoMinutos = entity.getDuracaoMinutos();
        this.tipo = entity.getTipo();
        this.status = entity.getStatus();
        this.linkAcesso = entity.getLinkAcesso();
        this.pacienteId = entity.getPaciente() != null ? entity.getPaciente().getId() : null;
        this.pacienteNome = entity.getPaciente() != null
                ? composeName(entity.getPaciente().getPrimeiroNome(), entity.getPaciente().getSobrenome())
                : null;
        this.medicoId = entity.getMedico() != null ? entity.getMedico().getId() : null;
        this.medicoNome = entity.getMedico() != null
                ? composeName(entity.getMedico().getNome(), entity.getMedico().getSobrenome())
                : null;
        this.motivo = entity.getMotivo();
    }

    private String composeName(String first, String second) {
        StringBuilder builder = new StringBuilder();
        if (first != null && !first.isBlank()) {
            builder.append(first.trim());
        }
        if (second != null && !second.isBlank()) {
            if (!builder.isEmpty()) {
                builder.append(" ");
            }
            builder.append(second.trim());
        }
        return builder.isEmpty() ? null : builder.toString();
    }
}
