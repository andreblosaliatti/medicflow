package com.inflowia.medicflow.security;

import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.repository.UsuarioRepository;
import com.inflowia.medicflow.exception.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.INVALID_LOGIN));

        Set<GrantedAuthority> authorities = AuthorizationMatrix.expandAuthorities(usuario.getRoles()
                .stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toSet()))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities(authorities)
                .disabled(!usuario.isAtivo())
                .build();
    }
}
