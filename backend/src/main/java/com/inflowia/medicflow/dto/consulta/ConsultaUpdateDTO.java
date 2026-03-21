package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
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

    public ConsultaUpdateDTO(
            LocalDateTime dataHora,
            TipoConsulta tipo,
            StatusConsulta status,
            Double valorConsulta,
            MeioPagamento meioPagamento,
            Boolean pago,
            LocalDateTime dataPagamento,
            Integer duracaoMinutos,
            Boolean retorno,
            LocalDateTime dataLimiteRetorno,
            Boolean teleconsulta,
            String linkAcesso,
            String planoSaude,
            String numeroCarteirinha,
            String motivo,
            String anamnese,
            String exameFisico,
            String diagnostico,
            String prescricao,
            String observacoes,
            Long pacienteId,
            Long medicoId
    ) {
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.status = status;
        this.valorConsulta = valorConsulta;
        this.meioPagamento = meioPagamento;
        this.pago = pago;
        this.dataPagamento = dataPagamento;
        this.duracaoMinutos = duracaoMinutos;
        this.retorno = retorno;
        this.dataLimiteRetorno = dataLimiteRetorno;
        this.teleconsulta = teleconsulta;
        this.linkAcesso = linkAcesso;
        this.planoSaude = planoSaude;
        this.numeroCarteirinha = numeroCarteirinha;
        this.motivo = motivo;
        this.anamnese = anamnese;
        this.exameFisico = exameFisico;
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.observacoes = observacoes;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
    }
}