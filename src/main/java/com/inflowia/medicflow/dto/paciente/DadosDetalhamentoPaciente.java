package com.inflowia.medicflow.dto.paciente;

import java.time.LocalDate;

import com.inflowia.medicflow.dto.DadosEndereco;
import com.inflowia.medicflow.entities.paciente.Paciente;

public record DadosDetalhamentoPaciente(
    Long id,
    String primeiroNome,
    String sobrenome,
    String email,
    String cpf,
    String Telefone,
    LocalDate dataNascimento,
    DadosEndereco endereco
) {
    public DadosDetalhamentoPaciente(Paciente paciente){
        this(
            paciente.getId(),
            paciente.getPrimeiroNome(),
                paciente.getSobrenome(),
            paciente.getEmail(),
            paciente.getCpf(),
            paciente.getTelefone(),
            paciente.getDataNascimento(),
            new DadosEndereco(paciente.getEndereco())
        );
    }
} 

