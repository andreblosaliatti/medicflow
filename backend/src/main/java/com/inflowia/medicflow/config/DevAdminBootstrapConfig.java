package com.inflowia.medicflow.config;

import com.inflowia.medicflow.entities.usuario.Role;
import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.RoleRepository;
import com.inflowia.medicflow.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Profile("dev")
public class DevAdminBootstrapConfig {

    @Bean
    public CommandLineRunner bootstrapAdmin(RoleRepository roleRepository,
                                            UsuarioRepository usuarioRepository,
                                            PasswordEncoder passwordEncoder,
                                            @Value("${app.bootstrap.admin.login:admin}") String adminLogin,
                                            @Value("${app.bootstrap.admin.password:admin123}") String adminPassword,
                                            @Value("${app.bootstrap.admin.email:admin@medicflow.local}") String adminEmail,
                                            @Value("${app.bootstrap.admin.cpf:32844208606}") String adminCpf,
                                            @Value("${app.bootstrap.admin.nome:System}") String adminNome,
                                            @Value("${app.bootstrap.admin.sobrenome:Admin}") String adminSobrenome) {
        return args -> {
            Role adminRole = roleRepository.findByAuthorityIgnoreCase("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

            Usuario adminUser = usuarioRepository.findByLoginIgnoreCase(adminLogin)
                    .orElseGet(() -> {
                        Usuario novo = new Usuario();
                        novo.setLogin(adminLogin);
                        novo.setSenha(passwordEncoder.encode(adminPassword));
                        novo.setEmail(adminEmail);
                        novo.setCpf(adminCpf);
                        novo.setNome(adminNome);
                        novo.setSobrenome(adminSobrenome);
                        novo.setAtivo(true);
                        novo.setRoles(new HashSet<>());
                        return usuarioRepository.save(novo);
                    });

            if (adminUser.getRoles().stream().noneMatch(role -> "ROLE_ADMIN".equalsIgnoreCase(role.getAuthority()))) {
                adminUser.getRoles().add(adminRole);
                usuarioRepository.save(adminUser);
            }
        };
    }
}
