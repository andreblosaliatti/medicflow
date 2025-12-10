package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.dto.RoleDTO;
import com.inflowia.medicflow.entities.usuario.Role;
import com.inflowia.medicflow.entities.usuario.Usuario;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class DadosDetalhamentoUsuario {

    private final Long id;
    private final String login;
    private final String nome;
    private final String sobrenome;
    private final String email;
    private final String cpf;
    private final Set<RoleDTO> roles;
    private final boolean ativo;
    private final EnderecoDTO endereco;

    // Construtor completo
    public DadosDetalhamentoUsuario(Long id,
                                    String login,
                                    String nome,
                                    String sobrenome,
                                    String email,
                                    String cpf,
                                    Set<RoleDTO> roles,
                                    boolean ativo,
                                    EnderecoDTO endereco) {
        this.id = id;
        this.login = login;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.cpf = cpf;
        this.roles = roles == null ? Collections.emptySet() : roles;
        this.ativo = ativo;
        this.endereco = endereco;
    }

    // Construtor que recebe Usuario (igual ao record)
    public DadosDetalhamentoUsuario(Usuario u) {
        this(
                u.getId(),
                u.getLogin(),
                u.getNome(),
                u.getSobrenome(),
                u.getEmail(),
                u.getCpf(),
                u.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet()),
                u.isAtivo(),
                u.getEndereco() == null ? null : new EnderecoDTO(u.getEndereco())
        );
    }

    // ---- Getters ----

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }
}
