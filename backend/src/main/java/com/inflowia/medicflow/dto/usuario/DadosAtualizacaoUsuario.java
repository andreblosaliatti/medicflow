package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.dto.RoleDTO;
import com.inflowia.medicflow.domain.usuario.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class DadosAtualizacaoUsuario {

    private String nome;

    private String sobrenome;

    @Email
    private String email;

    private Set<@Valid RoleDTO> roles;

    private Boolean ativo;

    @Valid
    private EnderecoDTO endereco;

    public DadosAtualizacaoUsuario() {}

    public DadosAtualizacaoUsuario(Usuario entity) {
        this.nome = entity.getNome();
        this.sobrenome = entity.getSobrenome();
        this.email = entity.getEmail();
        this.ativo = entity.isAtivo();
        this.roles = entity.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet());

        if (entity.getEndereco() != null) {
            this.endereco = new EnderecoDTO(entity.getEndereco());
        }
    }
}
