package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.dto.auth.LoginRequest;
import com.inflowia.medicflow.dto.auth.LoginResponse;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.repository.UsuarioRepository;
import com.inflowia.medicflow.security.AccessRole;
import com.inflowia.medicflow.security.AuthorizationMatrix;
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
import java.util.Set;

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
            throw new BadCredentialsException(ExceptionMessages.INACTIVE_USER);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(ExceptionMessages.INVALID_LOGIN);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getLogin());
        String token = jwtService.generateToken(userDetails);

        return buildResponse(request.getLogin(), token);
    }

    @Transactional(readOnly = true)
    public LoginResponse me(String login, String token) {
        return buildResponse(login, token);
    }

    private LoginResponse buildResponse(String login, String token) {
        Usuario usuario = usuarioRepository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new BadCredentialsException(ExceptionMessages.AUTHENTICATED_USER_NOT_FOUND));

        List<String> roles = AccessRole.toApiNames(usuario.getRoles()
                .stream()
                .map(role -> role.getAuthority())
                .toList());
        Set<String> permissions = AuthorizationMatrix.permissionsFor(usuario.getRoles()
                .stream()
                .map(role -> role.getAuthority())
                .collect(java.util.stream.Collectors.toSet()));

        String nomeCompleto = usuario.getNome() + " " + usuario.getSobrenome();

        return new LoginResponse(
                usuario.getId(),
                usuario.getLogin(),
                nomeCompleto,
                roles,
                permissions.stream().sorted().toList(),
                token
        );
    }
}
