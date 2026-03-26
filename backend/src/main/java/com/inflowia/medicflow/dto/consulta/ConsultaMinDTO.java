package com.inflowia.medicflow.dto.consulta;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ConsultaListDTO", description = "DTO de listagem resumida de consultas.")
public class ConsultaMinDTO {

    private Long id;
    private LocalDateTime dataHora;
    private TipoConsulta tipo;
    private StatusConsulta status;
    private BigDecimal valorConsulta;
    private Boolean pago;
    private Long pacienteId;
    private Long medicoId;

    public ConsultaMinDTO(Consulta entity) {
        this.id = entity.getId();
        this.dataHora = entity.getDataHora();
        this.tipo = entity.getTipo();
        this.status = entity.getStatus();
        this.valorConsulta = entity.getValorConsulta();
        this.pago = entity.getPago();
        this.pacienteId = entity.getPaciente() != null ? entity.getPaciente().getId() : null;
        this.medicoId = entity.getMedico() != null ? entity.getMedico().getId() : null;
    }
}
