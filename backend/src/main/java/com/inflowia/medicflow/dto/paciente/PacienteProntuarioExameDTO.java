package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.exame.StatusExame;

public class PacienteProntuarioExameDTO {

    private final String nome;
    private final StatusExame status;

    public PacienteProntuarioExameDTO(String nome, StatusExame status) {
        this.nome = nome;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public StatusExame getStatus() {
        return status;
    }
}
