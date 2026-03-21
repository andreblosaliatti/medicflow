package com.inflowia.medicflow.dto.paciente;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PacienteHistoricoResumoDTO {

    private long totalConsultas;
    private long totalExamesSolicitados;
    private long totalMedicamentosPrescritos;
    private LocalDateTime dataUltimaConsulta;
    private PacienteUltimaConsultaResumoDTO ultimaConsulta;
}
