package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.entities.usuario.Perfil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;


public record DadosCadastroUsuario(
        @NotBlank
        String login,

        @NotBlank
        @Size(min = 6, max = 100)
        String senha,

        @NotBlank
        String nome,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String sobrenome,

        @NotBlank
        @CPF
        String cpf,

        @NotNull
        Perfil perfil,

        @Valid
        EnderecoDTO endereco
        ){
        }

