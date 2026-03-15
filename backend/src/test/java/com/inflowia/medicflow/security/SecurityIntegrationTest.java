package com.inflowia.medicflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inflowia.medicflow.entities.usuario.Role;
import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.RoleRepository;
import com.inflowia.medicflow.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        Role roleAdmin = roleRepository.findByAuthorityIgnoreCase("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

        usuarioRepository.findByLoginIgnoreCase("security.test")
                .orElseGet(() -> {
                    Usuario user = new Usuario();
                    user.setLogin("security.test");
                    user.setSenha(passwordEncoder.encode("123456"));
                    user.setNome("Security");
                    user.setSobrenome("Test");
                    user.setEmail("security.test@medicflow.local");
                    user.setCpf("11144477735");
                    user.setAtivo(true);
                    user.setRoles(new HashSet<>());
                    user.getRoles().add(roleAdmin);
                    return usuarioRepository.save(user);
                });
    }

    @Test
    void protectedEndpointWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginEndpointShouldRemainPublicAndReturnTokenForValidCredentials() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("login", "security.test");
        request.put("senha", "123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void protectedEndpointWithInvalidTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/pacientes")
                        .header("Authorization", "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerDocsShouldBeAccessibleWithoutTokenInTestProfile() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
