package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.RoleRepository;
import com.inflowia.medicflow.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        service.insert(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertEquals("encoded", captor.getValue().getSenha());
    }
}
