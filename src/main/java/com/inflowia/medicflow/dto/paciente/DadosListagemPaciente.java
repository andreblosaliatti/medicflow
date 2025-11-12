package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.entities.paciente.Paciente;

public record DadosListagemPaciente (
      Long id,
      String primeiroNome,
      String sobrenome,
      String Telefone,
      String email,
      String cpf
) {
    public DadosListagemPaciente(Paciente paciente){
        this(paciente.getId(), paciente.getPrimeiroNome(), paciente.getSobrenome(), paciente.getTelefone(), paciente.getEmail(),paciente.getCpf());
    }
}


