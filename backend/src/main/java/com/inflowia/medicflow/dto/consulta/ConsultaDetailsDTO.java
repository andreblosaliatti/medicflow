package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoMinDTO;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ConsultaDetailDTO", description = "DTO de detalhe de consulta para leitura, incluindo dados agregados da consulta.")
public class ConsultaDetailsDTO {

    private Long id;
    private LocalDateTime dataHora;
    private TipoConsulta tipo;
    private StatusConsulta status;

    private BigDecimal valorConsulta;
    private MeioPagamento meioPagamento;

    private Boolean pago;
    private LocalDateTime dataPagamento;

    private Integer duracaoMinutos;

    private boolean retorno;
    private LocalDateTime dataLimiteRetorno;

    private String linkAcesso;

    private String planoSaude;
    private String numeroCarteirinha;

    private String motivo;
    private String anamnese;
    private String exameFisico;
    private String diagnostico;
    private String prescricao;
    private String observacoes;
    private ConsultaAcompanhamentoDTO acompanhamento;

    private String pacienteNome;
    private Long medicoId;
    private String medicoNome;
    private List<MedicamentoPrescritoMinDTO> medicamentosPrescritos;
    private List<ExameSolicitadoMinDTO> examesSolicitados;

    public ConsultaDetailsDTO(Consulta entity) {
        this.id = entity.getId();
        this.dataHora = entity.getDataHora();
        this.tipo = entity.getTipo();
        this.status = entity.getStatus();
        this.valorConsulta = entity.getValorConsulta();
        this.meioPagamento = entity.getMeioPagamento();
        this.pago = entity.getPago();
        this.dataPagamento = entity.getDataPagamento();
        this.duracaoMinutos = entity.getDuracaoMinutos();
        this.retorno = entity.isRetorno();
        this.dataLimiteRetorno = entity.getDataLimiteRetorno();
        this.linkAcesso = entity.getLinkAcesso();
        this.planoSaude = entity.getPlanoSaude();
        this.numeroCarteirinha = entity.getNumeroCarteirinha();
        this.motivo = entity.getMotivo();
        this.anamnese = entity.getAnamnese();
        this.exameFisico = entity.getExameFisico();
        this.diagnostico = entity.getDiagnostico();
        this.prescricao = entity.getPrescricao();
        this.observacoes = entity.getObservacoes();
        this.acompanhamento = new ConsultaAcompanhamentoDTO(entity);
        this.pacienteId = entity.getPaciente() != null ? entity.getPaciente().getId() : null;
        this.pacienteNome = entity.getPaciente() != null
                ? composeName(entity.getPaciente().getPrimeiroNome(), entity.getPaciente().getSobrenome())
                : null;
        this.medicoId = entity.getMedico() != null ? entity.getMedico().getId() : null;
        this.medicoNome = entity.getMedico() != null
                ? composeName(entity.getMedico().getNome(), entity.getMedico().getSobrenome())
                : null;
        this.medicamentosPrescritos = entity.getMedicamentoPrescrito() != null
                ? entity.getMedicamentoPrescrito().stream().map(MedicamentoPrescritoMinDTO::new).toList()
                : List.of();
        this.examesSolicitados = entity.getExameSolicitado() != null
                ? entity.getExameSolicitado().stream().map(ExameSolicitadoMinDTO::new).toList()
                : List.of();
    }

    private String composeName(String first, String second) {
        StringBuilder builder = new StringBuilder();
        if (first != null && !first.isBlank()) {
            builder.append(first.trim());
        }
        if (second != null && !second.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(second.trim());
        }
        return builder.length() == 0 ? null : builder.toString();
    }
}
