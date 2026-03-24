package com.inflowia.medicflow.dto.paciente;

import java.time.LocalDateTime;
import java.util.List;

public class PacienteProntuarioConsultaDTO {

    private final Long id;
    private final LocalDateTime dataHora;
    private final String medicoNome;
    private final String motivo;
    private final String anamnese;
    private final String exameFisico;
    private final String observacoes;
    private final List<PacienteProntuarioDiagnosticoDTO> diagnosticos;
    private final List<PacienteProntuarioMedicacaoDTO> medicacoes;
    private final List<PacienteProntuarioExameDTO> exames;

    public PacienteProntuarioConsultaDTO(Long id,
                                         LocalDateTime dataHora,
                                         String medicoNome,
                                         String motivo,
                                         String anamnese,
                                         String exameFisico,
                                         String observacoes,
                                         List<PacienteProntuarioDiagnosticoDTO> diagnosticos,
                                         List<PacienteProntuarioMedicacaoDTO> medicacoes,
                                         List<PacienteProntuarioExameDTO> exames) {
        this.id = id;
        this.dataHora = dataHora;
        this.medicoNome = medicoNome;
        this.motivo = motivo;
        this.anamnese = anamnese;
        this.exameFisico = exameFisico;
        this.observacoes = observacoes;
        this.diagnosticos = diagnosticos;
        this.medicacoes = medicacoes;
        this.exames = exames;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getAnamnese() {
        return anamnese;
    }

    public String getExameFisico() {
        return exameFisico;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public List<PacienteProntuarioDiagnosticoDTO> getDiagnosticos() {
        return diagnosticos;
    }

    public List<PacienteProntuarioMedicacaoDTO> getMedicacoes() {
        return medicacoes;
    }

    public List<PacienteProntuarioExameDTO> getExames() {
        return exames;
    }
}
