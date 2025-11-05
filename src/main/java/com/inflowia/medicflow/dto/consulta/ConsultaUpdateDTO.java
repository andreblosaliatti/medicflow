package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;

import java.time.LocalDateTime;

    public class ConsultaUpdateDTO {
        public LocalDateTime dataHora;
        public TipoConsulta tipo;
        public StatusConsulta status;
        public String motivo;
        public String anamnese;
        public String exameFisico;
        public String diagnostico;
        public String prescricao;
        public String observacoes;
    }

