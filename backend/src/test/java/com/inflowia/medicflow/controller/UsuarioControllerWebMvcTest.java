package com.inflowia.medicflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inflowia.medicflow.config.CorrelationIdFilter;
import com.inflowia.medicflow.dto.usuario.DadosCadastroUsuario;
import com.inflowia.medicflow.dto.usuario.DadosDetalhamentoUsuario;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.security.ApiAccessDeniedHandler;
import com.inflowia.medicflow.security.ApiAuthenticationEntryPoint;
import com.inflowia.medicflow.security.JwtAuthenticationFilter;
import com.inflowia.medicflow.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.inflowia.medicflow.exception.ControllerExceptionHandler.class)
@ActiveProfiles("test")
class UsuarioControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CorrelationIdFilter correlationIdFilter;

    @MockBean
    private ApiAuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    private ApiAccessDeniedHandler accessDeniedHandler;

    @Test
    void shouldReturnStandardizedValidationErrorWhenPayloadIsInvalid() throws Exception {
        DadosCadastroUsuario payload = new DadosCadastroUsuario();
        payload.setLogin("");
        payload.setSenha("123");
        payload.setNome("");
        payload.setSobrenome("");
        payload.setEmail("email-invalido");
        payload.setCpf("123");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(ErrorCodes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").value("Dados inválidos."))
                .andExpect(jsonPath("$.path").value("/usuarios"))
                .andExpect(jsonPath("$.errors.length()").value(6));
    }

    @Test
    void shouldReturnCreatedStatusAndLocationWhenPayloadIsValid() throws Exception {
        DadosCadastroUsuario payload = new DadosCadastroUsuario();
        payload.setLogin("novo.usuario");
        payload.setSenha("secret123");
        payload.setNome("Novo");
        payload.setSobrenome("Usuario");
        payload.setEmail("novo.usuario@test.com");
        payload.setCpf("32844208606");
        payload.setRoles(Set.of("ROLE_ADMIN"));

        when(usuarioService.insert(any(DadosCadastroUsuario.class))).thenReturn(
                new DadosDetalhamentoUsuario(
                        99L,
                        "novo.usuario",
                        "Novo",
                        "Usuario",
                        "novo.usuario@test.com",
                        "32844208606",
                        Set.of("ROLE_ADMIN"),
                        true,
                        null
                )
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/99"))
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.login").value("novo.usuario"));
    }
}
