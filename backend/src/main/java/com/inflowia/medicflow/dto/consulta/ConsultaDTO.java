package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ConsultaFormDTO", description = "DTO de formulário para cadastro completo de consultas.")
public class ConsultaDTO {

        @NotNull(message = "A data e hora da consulta são obrigatórias")
        @FutureOrPresent(message = "A data e hora da consulta deve ser no presente ou futuro")
        private LocalDateTime dataHora;

        @NotNull(message = "O tipo da consulta é obrigatório")
        private TipoConsulta tipo;

        @NotNull(message = "O status da consulta é obrigatório")
        private StatusConsulta status;

        @PositiveOrZero(message = "O valor da consulta não pode ser negativo")
        private BigDecimal valorConsulta;

        @NotNull(message = "O meio de pagamento é obrigatório")
        private MeioPagamento meioPagamento;

        private Boolean pago;
        private LocalDateTime dataPagamento;

        @Positive(message = "A duração da consulta deve ser maior que zero")
        private Integer duracaoMinutos;

        private boolean retorno;
        private LocalDateTime dataLimiteRetorno;
        
        @Size(max = 500, message = "O link de acesso deve ter no máximo 500 caracteres")
        private String linkAcesso;

        @Size(max = 255, message = "O plano de saúde deve ter no máximo 255 caracteres")
        private String planoSaude;

        @Size(max = 100, message = "O número da carteirinha deve ter no máximo 100 caracteres")
        private String numeroCarteirinha;

        @NotBlank(message = "O motivo da consulta é obrigatório")
        @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres")
        private String motivo;

        @NotNull(message = "O paciente da consulta é obrigatório")
        private Long pacienteId;

        @Size(max = 4000, message = "A anamnese deve ter no máximo 4000 caracteres")
        private String anamnese;

        @Size(max = 4000, message = "O exame físico deve ter no máximo 4000 caracteres")
        private String exameFisico;

        @Size(max = 4000, message = "O diagnóstico deve ter no máximo 4000 caracteres")
        private String diagnostico;

        @Size(max = 4000, message = "A prescrição deve ter no máximo 4000 caracteres")
        private String prescricao;

        @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres")
        private String observacoes;

        @NotNull(message = "O médico da consulta é obrigatório")
        private Long medicoId;
}
