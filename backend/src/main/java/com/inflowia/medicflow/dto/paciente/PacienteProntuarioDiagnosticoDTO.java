package com.inflowia.medicflow.dto.paciente;

public class PacienteProntuarioDiagnosticoDTO {

    private final String descricao;

    public PacienteProntuarioDiagnosticoDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
