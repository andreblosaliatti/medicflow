package com.inflowia.medicflow.security;

import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.repository.UsuarioRepository;
import com.inflowia.medicflow.support.AbstractIntegrationTest;
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
class SecurityIntegrationTest extends AbstractIntegrationTest {

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
    private String secretariaToken;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));
        Role secretariaRole = roleRepository.save(new Role(null, "ROLE_SECRETARIA"));

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

        Usuario secretaria = Usuario.builder()
                .login("secretaria.security")
                .senha(passwordEncoder.encode("secret123"))
                .nome("Secretaria")
                .sobrenome("Security")
                .email("secretaria.security@test.com")
                .cpf("61498182097")
                .ativo(true)
                .roles(Set.of(secretariaRole))
                .build();

        usuarioRepository.save(admin);
        usuarioRepository.save(medico);
        usuarioRepository.save(secretaria);

        adminToken = jwtService.generateToken(User.withUsername(admin.getLogin())
                .password(admin.getSenha())
                .authorities("ROLE_ADMIN")
                .build());

        medicoToken = jwtService.generateToken(User.withUsername(medico.getLogin())
                .password(medico.getSenha())
                .authorities("ROLE_MEDICO")
                .build());

        secretariaToken = jwtService.generateToken(User.withUsername(secretaria.getLogin())
                .password(secretaria.getSenha())
                .authorities("ROLE_SECRETARIA")
                .build());
    }

    @Test
    void shouldReturn401WhenRequestHasNoToken() throws Exception {
        mockMvc.perform(get("/test-security/admin").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_AUTHENTICATION_ERROR))
                .andExpect(jsonPath("$.message").value("Autenticação é obrigatória para acessar este recurso."))
                .andExpect(jsonPath("$.path").value("/test-security/admin"));
    }

    @Test
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/test-security/admin")
                        .header("Authorization", "Bearer token-invalido")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_AUTHENTICATION_ERROR))
                .andExpect(jsonPath("$.path").value("/test-security/admin"));
    }

    @Test
    void shouldReturn403WhenAuthenticatedUserHasWrongRole() throws Exception {
        mockMvc.perform(get("/test-security/admin")
                        .header("Authorization", "Bearer " + medicoToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED))
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

    @Test
    void shouldAllowAccessWhenRoleReceivesPermissionForSpecificResource() throws Exception {
        mockMvc.perform(get("/test-security/consultas")
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("ok"));
    }

    @Test
    void shouldDenyAccessWhenRoleDoesNotReceiveResourcePermission() throws Exception {
        mockMvc.perform(get("/test-security/usuarios")
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
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

        @GetMapping("/consultas")
        @PreAuthorize("hasAuthority('consultas:read')")
        public java.util.Map<String, String> consultasRead() {
            return java.util.Map.of("result", "ok");
        }

        @GetMapping("/usuarios")
        @PreAuthorize("hasAuthority('usuarios:read')")
        public java.util.Map<String, String> usuariosRead() {
            return java.util.Map.of("result", "ok");
        }
    }
}
