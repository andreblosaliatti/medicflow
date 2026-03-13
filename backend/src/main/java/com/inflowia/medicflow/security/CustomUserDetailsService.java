package com.inflowia.medicflow.security;

import com.inflowia.medicflow.entities.usuario.Usuario;
import com.inflowia.medicflow.repositories.UsuarioRepository;
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
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para login: " + login));

        Set<GrantedAuthority> authorities = usuario.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());

        return User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities(authorities)
                .disabled(!usuario.isAtivo())
                .build();
    }
}