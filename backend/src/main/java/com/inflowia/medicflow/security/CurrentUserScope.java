package com.inflowia.medicflow.security;

import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserScope {

    private final MedicoRepository medicoRepository;

    public boolean requiresMedicoScope() {
        Authentication authentication = currentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return hasAuthority(authentication, AccessRole.MEDICO.authority())
                && !hasAuthority(authentication, AccessRole.ADMIN.authority())
                && !hasAuthority(authentication, AccessRole.SECRETARIA.authority());
    }

    public Long requireMedicoId() {
        Authentication authentication = currentAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw accessDenied();
        }

        return medicoRepository.findByLoginIgnoreCaseAndAtivoTrue(authentication.getName())
                .map(Medico::getId)
                .orElseThrow(this::accessDenied);
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }

    private AccessDeniedException accessDenied() {
        return new AccessDeniedException(ExceptionMessages.ACCESS_DENIED);
    }
}
