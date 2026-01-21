package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.entities.usuario.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.br.CPF;

import java.util.Set;

@Getter
@Setter
public class DadosCadastroUsuario {

    @NotBlank
    private String login;

    @NotBlank
    @Size(min = 6, max = 100)
    private String senha;

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String sobrenome;

    @NotBlank
    @CPF
    private String cpf;

    // Agora usando ROLE ao invés de Perfil
    private Set<Role> roles;

    @Valid
    private EnderecoDTO endereco;

    public DadosCadastroUsuario() {}

    public DadosCadastroUsuario(String login, String senha, String nome, String email,
                                String sobrenome, String cpf, Set<Role> roles,
                                EnderecoDTO endereco) {

        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
        this.sobrenome = sobrenome;
        this.cpf = cpf;
        this.roles = roles;
        this.endereco = endereco;
    }
}
