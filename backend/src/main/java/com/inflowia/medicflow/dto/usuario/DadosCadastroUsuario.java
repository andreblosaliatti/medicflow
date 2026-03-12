package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.dto.RoleDTO;
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

    private Set<@Valid RoleDTO> roles;

    @Valid
    private EnderecoDTO endereco;
}
