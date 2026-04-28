package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.repository.UsuarioRepository;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.security.JwtService;
import com.inflowia.medicflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private Usuario usuario;
    private String token;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));

        Medico medico = new Medico();
        medico.setLogin("auth.user");
        medico.setSenha(passwordEncoder.encode("secret123"));
        medico.setNome("Auth");
        medico.setSobrenome("User");
        medico.setEmail("auth.user@test.com");
        medico.setCpf("95633733088");
        medico.setAtivo(true);
        medico.setRoles(Set.of(adminRole, medicoRole));
        medico.setCrm("CRM-12345");
        medico.setEspecialidade("Clinica geral");

        usuario = usuarioRepository.save(medico);

        token = jwtService.generateToken(User.withUsername(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities("ROLE_ADMIN", "ROLE_MEDICO")
                .build());
    }

    @Test
    void shouldLoginWithStandardizedPayload() throws Exception {
        mockMvc.perform(post(ApiPaths.AUTH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"login\": \"auth.user\",
                                  \"senha\": \"secret123\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.login").value("auth.user"))
                .andExpect(jsonPath("$.nomeCompleto").value("Auth User"))
                .andExpect(jsonPath("$.medicoId").value(usuario.getId()))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("MEDICO"))
                .andExpect(jsonPath("$.permissions").isArray())
                .andExpect(jsonPath("$.permissions").value(hasItem("usuarios:write")))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.type").doesNotExist());
    }

    @Test
    void shouldReturnAuthenticatedUserFromMeEndpoint() throws Exception {
        mockMvc.perform(get(ApiPaths.AUTH + "/me")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.login").value("auth.user"))
                .andExpect(jsonPath("$.nomeCompleto").value("Auth User"))
                .andExpect(jsonPath("$.medicoId").value(usuario.getId()))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("MEDICO"))
                .andExpect(jsonPath("$.permissions").value(hasItem("consultas:write")))
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void shouldRequireAuthenticationForMeEndpoint() throws Exception {
        mockMvc.perform(get(ApiPaths.AUTH + "/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_AUTHENTICATION_ERROR))
                .andExpect(jsonPath("$.path").value(ApiPaths.AUTH + "/me"));
    }
}
