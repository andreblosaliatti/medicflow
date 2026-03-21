package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DadosAtualizacaoUsuario {

    private String login;
    private String nome;
    private String sobrenome;

    @Email
    private String email;

    private Set<String> roles;
    private Boolean ativo;

    @Valid
    private EnderecoDTO endereco;
}
