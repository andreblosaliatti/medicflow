package com.inflowia.medicflow.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class DadosListagemUsuario {

    private Long id;
    private String login;
    private String nome;
    private String sobrenome;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private Boolean ativo;
    private Set<String> roles;

    public DadosListagemUsuario() {
        this.roles = new HashSet<>();
    }

    public Set<String> getRoles() {
        return roles == null ? Collections.emptySet() : roles;
    }
}
