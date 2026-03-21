package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.paciente.Paciente;
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
    private String planoSaude;
    private boolean ativo;

    public PacienteDetailsDTO(Paciente p) {
        this.id = p.getId();
        this.primeiroNome = p.getPrimeiroNome();
        this.sobrenome = p.getSobrenome();
        this.cpf = p.getCpf();
        this.dataNascimento = p.getDataNascimento();
        this.telefone = p.getTelefone();
        this.email = p.getEmail();
        this.sexo = p.getSexo();
        this.endereco = p.getEndereco() != null ? new EnderecoDTO(p.getEndereco()) : null;
        this.planoSaude = p.getPlanoSaude();
        this.ativo = p.isAtivo();
    }
}
