package com.inflowia.medicflow.mappers;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosListagemUsuario;
import com.inflowia.medicflow.entities.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public DadosDetalhamentoUsuario toDetalhamento(Usuario entity) {
        if (entity == null) {
            return null;
        }

        return new DadosDetalhamentoUsuario(
                entity.getId(),
                entity.getLogin(),
                entity.getNome(),
                entity.getSobrenome(),
                entity.getEmail(),
                entity.getCpf(),
                extractAuthorities(entity),
                entity.isAtivo(),
                entity.getEndereco() == null ? null : new EnderecoDTO(entity.getEndereco())
        );
    }

    public DadosListagemUsuario toListagem(Usuario entity) {
        if (entity == null) {
            return null;
        }

        return new DadosListagemUsuario(
                entity.getId(),
                entity.getLogin(),
                entity.getNome(),
                entity.getSobrenome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.isAtivo(),
                extractAuthorities(entity)
        );
    }

    private Set<String> extractAuthorities(Usuario entity) {
        return entity.getRoles()
                .stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toSet());
    }
}