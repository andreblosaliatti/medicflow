package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.entities.paciente.Paciente;
import lombok.Getter;
import com.inflowia.medicflow.dto.EnderecoDTO;

import java.time.LocalDate;

@Getter
public class PacienteDetailsDTO {

    private Long id;
    private String primeiroNome;
    private String sobrenome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String sexo;
    private EnderecoDTO endereco;

    public PacienteDetailsDTO(Paciente p) {
        this.id = p.getId();
        this.primeiroNome = p.getPrimeiroNome();
        this.sobrenome = p.getSobrenome();
        this.cpf = p.getCpf();
        this.dataNascimento = p.getDataNascimento();
        this.telefone = p.getTelefone();
        this.email = p.getEmail();
        this.sexo = p.getSexo();
        this.endereco = new EnderecoDTO(p.getEndereco());
    }
}
