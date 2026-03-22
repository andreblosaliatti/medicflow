package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class DadosDetalhamentoUsuario {

    private Long id;
    private String login;
    private String nome;
    private String sobrenome;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private Set<String> roles;
    private boolean ativo;
    private EnderecoDTO endereco;

    public DadosDetalhamentoUsuario() {
        this.roles = new HashSet<>();
    }

    public Set<String> getRoles() {
        return roles == null ? Collections.emptySet() : roles;
    }
}
