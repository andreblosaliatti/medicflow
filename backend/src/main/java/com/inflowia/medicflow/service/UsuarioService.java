package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.dto.usuario.DadosAtualizacaoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.dto.usuario.DadosListagemUsuario;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import com.inflowia.medicflow.mappers.UsuarioMapper;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.repository.UsuarioRepository;
import com.inflowia.medicflow.security.AccessRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public Page<DadosListagemUsuario> findAllPaged(String nome, Boolean ativo, String role, Pageable pageable) {
        String normalizedRole = normalizeRole(role);
        Page<Usuario> page = repository.searchForAdmin(nome == null ? "" : nome, ativo, normalizedRole, pageable);
        return page.map(usuarioMapper::toListagem);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario findById(Long id) {
        Usuario entity = getUsuarioAtivo(id);
        return usuarioMapper.toDetalhamento(entity);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario findByCpf(String cpf) {
        Usuario entity = repository.findByCpfAndAtivoTrue(cpf)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.notFoundBy("Usuário", "CPF", cpf)
                ));
        return usuarioMapper.toDetalhamento(entity);
    }

    @Transactional
    public DadosDetalhamentoUsuario insert(DadosCadastroUsuario dto) {
        Usuario entity = new Usuario();

        entity.setLogin(dto.getLogin());
        entity.setSenha(passwordEncoder.encode(dto.getSenha()));
        entity.setNome(dto.getNome());
        entity.setSobrenome(dto.getSobrenome());
        entity.setEmail(dto.getEmail());
        entity.setCpf(dto.getCpf());
        entity.setAtivo(dto.getAtivo() == null || dto.getAtivo());

        if (dto.getEndereco() != null) {
            entity.setEndereco(dto.getEndereco().toEntity());
        }

        entity.setRoles(resolveRoles(dto.getRoles()));

        entity = repository.save(entity);
        return usuarioMapper.toDetalhamento(entity);
    }

    @Transactional
    public DadosDetalhamentoUsuario update(Long id, DadosAtualizacaoUsuario dto) {
        Usuario entity = getUsuarioAtivo(id);

        if (dto.getLogin() != null) {
            entity.setLogin(dto.getLogin());
        }
        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        if (dto.getSobrenome() != null) {
            entity.setSobrenome(dto.getSobrenome());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getAtivo() != null) {
            entity.setAtivo(dto.getAtivo());
        }
        if (dto.getRoles() != null) {
            entity.setRoles(resolveRoles(dto.getRoles()));
        }
        if (dto.getEndereco() != null) {
            entity.setEndereco(dto.getEndereco().toEntity());
        }

        entity = repository.save(entity);
        return usuarioMapper.toDetalhamento(entity);
    }

    @Transactional
    public void delete(Long id) {
        Usuario entity = getUsuarioAtivo(id);
        entity.setAtivo(false);
        repository.save(entity);
    }

    private Usuario getUsuarioAtivo(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Usuário")));
    }

    private Set<Role> resolveRoles(Set<String> roleAuthorities) {
        if (roleAuthorities == null || roleAuthorities.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Role> resolvedRoles = new HashSet<>();

        for (String authority : AccessRole.toCanonicalAuthorities(roleAuthorities)) {
            Role role = roleRepository.findByAuthority(authority)
                    .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + authority));
            resolvedRoles.add(role);
        }

        return resolvedRoles;
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        return AccessRole.fromValue(role).authority();
    }
}
