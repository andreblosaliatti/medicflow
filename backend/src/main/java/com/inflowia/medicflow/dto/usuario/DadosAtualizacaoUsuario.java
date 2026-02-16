package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.entities.usuario.Role;
import com.inflowia.medicflow.entities.usuario.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DadosAtualizacaoUsuario {

    private String nome;

    private String sobrenome;

    @Email
    private String email;

    private Set<Role> roles;

    private Boolean ativo;

    @Valid
    private EnderecoDTO endereco;

    public DadosAtualizacaoUsuario() {}

    public DadosAtualizacaoUsuario(Usuario entity) {
        this.nome = entity.getNome();
        this.sobrenome = entity.getSobrenome();
        this.email = entity.getEmail();
        this.ativo = entity.isAtivo();
        this.roles = entity.getRoles();

        if (entity.getEndereco() != null) {
            this.endereco = new EnderecoDTO(entity.getEndereco());
        }
    }
}

