package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.auth.LoginRequest;
import com.inflowia.medicflow.dto.auth.LoginResponse;
import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.UsuarioRepository;
import com.inflowia.medicflow.security.CustomUserDetailsService;
import com.inflowia.medicflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getSenha()
                    )
            );
        } catch (DisabledException e) {
            throw new BadCredentialsException("Usuário inativo");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Login ou senha inválidos");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getLogin());

        Usuario usuario = usuarioRepository.findByLoginIgnoreCase(request.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado após autenticação"));

        String token = jwtService.generateToken(userDetails);

        List<String> roles = usuario.getRoles()
                .stream()
                .map(role -> role.getAuthority())
                .sorted()
                .toList();

        String nomeCompleto = usuario.getNome() + " " + usuario.getSobrenome();

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getLogin(),
                nomeCompleto,
                roles
        );
    }
}