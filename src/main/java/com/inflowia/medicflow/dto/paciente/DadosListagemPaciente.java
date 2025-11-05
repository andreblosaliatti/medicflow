package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.entities.paciente.Paciente;

public record DadosListagemPaciente (
      Long id,
      String nome,
      String Telefone,
      String email,
      String cpf
) {
    public DadosListagemPaciente(Paciente paciente){
        this(paciente.getId(), paciente.getNome(), paciente.getTelefone(), paciente.getEmail(),paciente.getCpf());
    }
}


