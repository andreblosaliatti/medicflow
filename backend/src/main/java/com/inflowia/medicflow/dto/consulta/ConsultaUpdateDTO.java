package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.entities.consulta.MeioPagamento;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaUpdateDTO {

    @FutureOrPresent(message = "A data e hora da consulta deve ser no presente ou futuro")
    private LocalDateTime dataHora;

    private TipoConsulta tipo;
    private StatusConsulta status;

    @PositiveOrZero(message = "O valor da consulta não pode ser negativo")
    private Double valorConsulta;

    private MeioPagamento meioPagamento;
    private Boolean pago;
    private LocalDateTime dataPagamento;

    @Positive(message = "A duração da consulta deve ser maior que zero")
    private Integer duracaoMinutos;

    private Boolean retorno;
    private LocalDateTime dataLimiteRetorno;
    private Boolean teleconsulta;

    @Size(max = 500, message = "O link de acesso deve ter no máximo 500 caracteres")
    private String linkAcesso;

    @Size(max = 255, message = "O plano de saúde deve ter no máximo 255 caracteres")
    private String planoSaude;

    @Size(max = 100, message = "O número da carteirinha deve ter no máximo 100 caracteres")
    private String numeroCarteirinha;

    @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres")
    private String motivo;

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

    private Long pacienteId;
    private Long medicoId;
}
