package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.MeioPagamento;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDetailsDTO {

    private Long id;
    private LocalDateTime dataHora;
    private TipoConsulta tipo;
    private StatusConsulta status;

    private Double valorConsulta;
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

    private String motivo;
    private String anamnese;
    private String exameFisico;
    private String diagnostico;
    private String prescricao;
    private String observacoes;

    private Long pacienteId;
    private Long medicoId;

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
        this.teleconsulta = entity.isTeleconsulta();
        this.linkAcesso = entity.getLinkAcesso();
        this.planoSaude = entity.getPlanoSaude();
        this.numeroCarteirinha = entity.getNumeroCarteirinha();
        this.motivo = entity.getMotivo();
        this.anamnese = entity.getAnamnese();
        this.exameFisico = entity.getExameFisico();
        this.diagnostico = entity.getDiagnostico();
        this.prescricao = entity.getPrescricao();
        this.observacoes = entity.getObservacoes();
        this.pacienteId = entity.getPaciente() != null ? entity.getPaciente().getId() : null;
        this.medicoId = entity.getMedico() != null ? entity.getMedico().getId() : null;
    }
}
