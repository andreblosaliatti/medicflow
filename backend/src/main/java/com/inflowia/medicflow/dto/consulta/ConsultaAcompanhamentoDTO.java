package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.domain.consulta.Consulta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaAcompanhamentoDTO {

    private String anamnese;
    private String exameFisico;
    private String diagnostico;
    private String prescricao;
    private String observacoes;

    public ConsultaAcompanhamentoDTO(Consulta entity) {
        this.anamnese = entity.getAnamnese();
        this.exameFisico = entity.getExameFisico();
        this.diagnostico = entity.getDiagnostico();
        this.prescricao = entity.getPrescricao();
        this.observacoes = entity.getObservacoes();
    }
}
