package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;

import java.time.LocalDateTime;

    public class ConsultaResponseDTO {
        public Long id;
        public LocalDateTime dataHora;
        public TipoConsulta tipo;
        public StatusConsulta status;
        public String motivo;
        public Long pacienteId;
        public String pacienteNome;
        public Long medicoId;
        public String medicoNome;
    }


