package com.inflowia.medicflow.security;

import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String medicoToken;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));

        Usuario admin = Usuario.builder()
                .login("admin.security")
                .senha(passwordEncoder.encode("secret123"))
                .nome("Admin")
                .sobrenome("Security")
                .email("admin.security@test.com")
                .cpf("32844208606")
                .ativo(true)
                .roles(Set.of(adminRole))
                .build();

        Usuario medico = Usuario.builder()
                .login("medico.security")
                .senha(passwordEncoder.encode("secret123"))
                .nome("Medico")
                .sobrenome("Security")
                .email("medico.security@test.com")
                .cpf("39053344705")
                .ativo(true)
                .roles(Set.of(medicoRole))
                .build();

        usuarioRepository.save(admin);
        usuarioRepository.save(medico);

        adminToken = jwtService.generateToken(User.withUsername(admin.getLogin())
                .password(admin.getSenha())
                .authorities("ROLE_ADMIN")
                .build());

        medicoToken = jwtService.generateToken(User.withUsername(medico.getLogin())
                .password(medico.getSenha())
                .authorities("ROLE_MEDICO")
                .build());
    }

    @Test
    void shouldReturn401WhenRequestHasNoToken() throws Exception {
        mockMvc.perform(get("/test-security/admin").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Autenticação é obrigatória para acessar este recurso."))
                .andExpect(jsonPath("$.path").value("/test-security/admin"));
    }

    @Test
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/test-security/admin")
                        .header("Authorization", "Bearer token-invalido")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/test-security/admin"));
    }

    @Test
    void shouldReturn403WhenAuthenticatedUserHasWrongRole() throws Exception {
        mockMvc.perform(get("/test-security/admin")
                        .header("Authorization", "Bearer " + medicoToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("Você não tem permissão para realizar esta operação."))
                .andExpect(jsonPath("$.path").value("/test-security/admin"));
    }

    @Test
    void shouldAllowAccessWhenAuthenticatedUserHasRequiredRole() throws Exception {
        mockMvc.perform(get("/test-security/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("ok"));
    }

    @TestConfiguration
    static class TestSecurityControllerConfig {

        @Bean
        TestSecurityController testSecurityController() {
            return new TestSecurityController();
        }
    }

    @RestController
    @RequestMapping("/test-security")
    static class TestSecurityController {

        @GetMapping("/admin")
        @PreAuthorize("hasRole('ADMIN')")
        public java.util.Map<String, String> adminOnly() {
            return java.util.Map.of("result", "ok");
        }
    }
}
