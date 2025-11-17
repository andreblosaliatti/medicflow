package com.inflowia.medicflow.dto.usuario;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.entities.usuario.Perfil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoUsuario(
        @NotNull Long id,
        String nome,
        String sobrenome,
        @Email String email,
        Perfil perfil,
        Boolean ativo,
        @Valid EnderecoDTO endereco
) {}
