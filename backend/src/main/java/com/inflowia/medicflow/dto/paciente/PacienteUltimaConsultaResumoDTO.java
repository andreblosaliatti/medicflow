package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PacienteUltimaConsultaResumoDTO {

    private final Long id;
    private final LocalDateTime dataHora;
    private final TipoConsulta tipo;
    private final StatusConsulta status;
    private final String motivo;
    private final String medicoNome;

    public PacienteUltimaConsultaResumoDTO(Consulta consulta) {
        this.id = consulta.getId();
        this.dataHora = consulta.getDataHora();
        this.tipo = consulta.getTipo();
        this.status = consulta.getStatus();
        this.motivo = consulta.getMotivo();
        this.medicoNome = consulta.getMedico() != null
                ? (consulta.getMedico().getNome() + " " + consulta.getMedico().getSobrenome()).trim()
                : null;
    }
}
