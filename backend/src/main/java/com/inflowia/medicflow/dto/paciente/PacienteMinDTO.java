package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.paciente.Paciente;
import lombok.Getter;

@Getter
public class PacienteMinDTO {

    private Long id;
    private String nomeCompleto;
    private String telefone;
    private String planoSaude;

    public PacienteMinDTO(Paciente p) {
        this.id = p.getId();
        this.nomeCompleto = (p.getNome() + " " + p.getSobrenome()).trim();
        this.telefone = p.getTelefone();
        this.planoSaude = p.getPlanoSaude();
    }
}
