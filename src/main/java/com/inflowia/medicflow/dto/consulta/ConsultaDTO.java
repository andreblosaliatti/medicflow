package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.entities.consulta.MeioPagamento;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDTO {

        @NotNull
        @FutureOrPresent
        private LocalDateTime dataHora;

        @NotNull
        private TipoConsulta tipo;

        @NotNull
        private StatusConsulta status;

        @PositiveOrZero
        private Double valorConsulta;

        @NotNull
        private MeioPagamento meioPagamento;

        private Boolean pago;
        private LocalDateTime dataPagamento;

        private Integer duracaoMinutos;

        private boolean retorno;
        private LocalDateTime dataLimiteRetorno;

        private boolean teleconsulta;
        private String linkAcesso;

        private String planoSaude;
        private String numeroCarteirinha;

        @Size(max = 500)
        private String motivo;

        @Size(max = 4000)
        private String anamnese;

        @Size(max = 4000)
        private String exameFisico;

        @Size(max = 4000)
        private String diagnostico;

        @Size(max = 4000)
        private String prescricao;

        @Size(max = 2000)
        private String observacoes;

        @NotNull
        private Long pacienteId;

        @NotNull
        private Long medicoId;
}
