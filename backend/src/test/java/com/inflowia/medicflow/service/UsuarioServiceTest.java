package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.mappers.UsuarioMapper;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository repository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UsuarioMapper usuarioMapper;

    @InjectMocks
    UsuarioService service;

    @Test
    void insertShouldAlwaysEncodePassword() {
        DadosCadastroUsuario dto = new DadosCadastroUsuario();
        dto.setLogin("user");
        dto.setSenha("plain123");
        dto.setNome("Nome");
        dto.setSobrenome("Sobrenome");
        dto.setEmail("u@x.com");
        dto.setCpf("39053344705");

        when(passwordEncoder.encode("plain123")).thenReturn("encoded");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioMapper.toDetalhamento(any(Usuario.class))).thenReturn(new DadosDetalhamentoUsuario());

        service.insert(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertEquals("encoded", captor.getValue().getSenha());
    }


    @Test
    void insertShouldResolveRolesAndPersistActiveUser() {
        DadosCadastroUsuario dto = new DadosCadastroUsuario();
        dto.setLogin("admin");
        dto.setSenha("plain123");
        dto.setNome("Nome");
        dto.setSobrenome("Sobrenome");
        dto.setEmail("admin@test.com");
        dto.setCpf("32844208606");
        dto.setRoles(Set.of("ROLE_ADMIN"));

        Role adminRole = new Role(1L, "ROLE_ADMIN");

        when(passwordEncoder.encode("plain123")).thenReturn("encoded");
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioMapper.toDetalhamento(any(Usuario.class))).thenReturn(new DadosDetalhamentoUsuario());

        service.insert(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertTrue(salvo.isAtivo());
        assertEquals(Set.of(adminRole), salvo.getRoles());
    }

    @Test
    void updateShouldChangeOnlyProvidedFieldsAndKeepExistingValues() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .login("user")
                .senha("encoded")
                .nome("Nome Original")
                .sobrenome("Sobrenome Original")
                .email("original@test.com")
                .cpf("39053344705")
                .ativo(true)
                .roles(Set.of(new Role(2L, "ROLE_MEDICO")))
                .build();

        com.inflowia.medicflow.dto.usuario.DadosAtualizacaoUsuario dto = new com.inflowia.medicflow.dto.usuario.DadosAtualizacaoUsuario();
        dto.setNome("Nome Atualizado");
        dto.setAtivo(false);

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioMapper.toDetalhamento(any(Usuario.class))).thenReturn(new DadosDetalhamentoUsuario());

        service.update(1L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        Usuario atualizado = captor.getValue();
        assertEquals("Nome Atualizado", atualizado.getNome());
        assertEquals("Sobrenome Original", atualizado.getSobrenome());
        assertEquals("original@test.com", atualizado.getEmail());
        assertFalse(atualizado.isAtivo());
        assertSame(usuario, atualizado);
    }

    @Test
    void deleteShouldInactivateUser() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .login("user")
                .senha("encoded")
                .nome("Nome")
                .sobrenome("Sobrenome")
                .email("u@x.com")
                .cpf("39053344705")
                .ativo(true)
                .build();

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.delete(1L);

        assertFalse(usuario.isAtivo());
        verify(repository).save(usuario);
    }
}