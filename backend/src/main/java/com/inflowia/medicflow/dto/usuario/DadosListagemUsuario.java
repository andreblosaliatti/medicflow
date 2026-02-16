package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.RoleDTO;
import com.inflowia.medicflow.entities.usuario.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DadosListagemUsuario {

    private final Long id;
    private final String login;
    private final String nome;
    private final String sobrenome;
    private final String email;
    private final String cpf;
    private final Boolean ativo;

    Set<RoleDTO> roles = new HashSet<>();

    // Construtor completo (usado pelo controller/service ao montar resposta)
    public DadosListagemUsuario(Long id,
                                String login,
                                String nome,
                                String sobrenome,
                                String email,
                                String cpf,
                                Boolean ativo) {

        this.id = id;
        this.login = login;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.cpf = cpf;
        this.ativo = ativo;
    }

    // Construtor que aceita Usuario (idêntico ao record original)
    public DadosListagemUsuario(Usuario u) {
               id =  u.getId();
               login = u.getLogin();
               nome = u.getNome();
               sobrenome = u.getSobrenome();
               email = u.getEmail();
               cpf = u.getCpf();
               ativo = u.isAtivo();
               u.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
    }
}
