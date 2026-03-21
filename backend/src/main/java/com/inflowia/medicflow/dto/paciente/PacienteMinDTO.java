package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.paciente.Paciente;
import lombok.Getter;

@Getter
public class PacienteMinDTO {

    private Long id;
    private String nomeCompleto;
    private String telefone;

    public PacienteMinDTO(Paciente p) {
        this.id = p.getId();
        this.nomeCompleto = p.getPrimeiroNome() + " " + p.getSobrenome();
        this.telefone = p.getTelefone();
    }
}

