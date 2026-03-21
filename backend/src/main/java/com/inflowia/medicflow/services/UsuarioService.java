package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.usuario.*;
import com.inflowia.medicflow.entities.usuario.Role;
import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.RoleRepository;
import com.inflowia.medicflow.repositories.UsuarioRepository;
import com.inflowia.medicflow.services.exceptions.BusinessRuleException;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<DadosListagemUsuario> findAllPaged(String nome, Pageable pageable) {
        Page<Usuario> page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        return page.map(DadosListagemUsuario::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario findById(Long id) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Usuário")));
        return new DadosDetalhamentoUsuario(entity);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario findByCpf(String cpf) {
        Usuario entity = repository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFoundBy("Usuário", "CPF", cpf)));
        return new DadosDetalhamentoUsuario(entity);
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
        entity.setAtivo(true);

        if (dto.getEndereco() != null) {
            entity.setEndereco(dto.getEndereco().toEntity());
        }

        if (dto.getRoles() != null) {
            Set<Role> roles = dto.getRoles()
                    .stream()
                    .map(r -> roleRepository.getReferenceById(r.getId()))
                    .collect(Collectors.toSet());
            entity.setRoles(roles);
        }

        entity = repository.save(entity);
        return new DadosDetalhamentoUsuario(entity);
    }

    @Transactional
    public DadosAtualizacaoUsuario update(Long id, DadosAtualizacaoUsuario dto) {
        try {
            Usuario entity = repository.getReferenceById(id);

            if (dto.getNome() != null) entity.setNome(dto.getNome());
            if (dto.getSobrenome() != null) entity.setSobrenome(dto.getSobrenome());
            if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
            if (dto.getAtivo() != null) entity.setAtivo(dto.getAtivo());

            if (dto.getRoles() != null) {
                Set<Role> roles = dto.getRoles()
                        .stream()
                        .map(r -> roleRepository.getReferenceById(r.getId()))
                        .collect(Collectors.toSet());
                entity.setRoles(roles);
            }

            if (dto.getEndereco() != null) {
                entity.setEndereco(dto.getEndereco().toEntity());
            }

            repository.save(entity);
            return new DadosAtualizacaoUsuario(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Usuário"));
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFoundBy("Usuário", "id", String.valueOf(id)));
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("Não é possível excluir o usuário informado porque ele está vinculado a outros registros.");
        }
    }
}