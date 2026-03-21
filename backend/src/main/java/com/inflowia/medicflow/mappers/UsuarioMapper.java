package com.inflowia.medicflow.mappers;

import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosListagemUsuario;
import com.inflowia.medicflow.security.AccessRole;
import org.springframework.stereotype.Component;

import java.util.Set;

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
        return new java.util.LinkedHashSet<>(AccessRole.toApiNames(entity.getRoles()
                .stream()
                .map(role -> role.getAuthority())
                .toList()));
    }
}
