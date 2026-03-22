package com.inflowia.medicflow.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class AuthorizationMatrix {

    private AuthorizationMatrix() {
    }

    public static Set<String> expandAuthorities(Collection<String> authorities) {
        Set<String> expanded = new LinkedHashSet<>();
        if (authorities == null) {
            return expanded;
        }

        for (String authority : authorities) {
            AccessRole role = AccessRole.fromValue(authority);
            expanded.add(role.authority());
            expanded.addAll(permissionsFor(role));
        }

        return expanded;
    }

    public static Set<String> permissionsFor(Collection<String> authorities) {
        Set<String> permissions = new LinkedHashSet<>();
        if (authorities == null) {
            return permissions;
        }

        for (String authority : authorities) {
            permissions.addAll(permissionsFor(AccessRole.fromValue(authority)));
        }

        return permissions;
    }

    private static Set<String> permissionsFor(AccessRole role) {
        return switch (role) {
            case ADMIN -> Set.of(
                    Permissions.USUARIOS_READ,
                    Permissions.USUARIOS_WRITE,
                    Permissions.PACIENTES_READ,
                    Permissions.PACIENTES_WRITE,
                    Permissions.MEDICOS_READ,
                    Permissions.MEDICOS_WRITE,
                    Permissions.CONSULTAS_READ,
                    Permissions.CONSULTAS_WRITE,
                    Permissions.CONSULTAS_DELETE,
                    Permissions.EXAMES_BASE_READ,
                    Permissions.EXAMES_BASE_WRITE,
                    Permissions.EXAMES_SOLICITADOS_READ,
                    Permissions.EXAMES_SOLICITADOS_WRITE,
                    Permissions.MEDICAMENTOS_BASE_READ,
                    Permissions.MEDICAMENTOS_READ,
                    Permissions.MEDICAMENTOS_WRITE
            );
            case MEDICO -> Set.of(
                    Permissions.PACIENTES_READ,
                    Permissions.PACIENTES_WRITE,
                    Permissions.MEDICOS_READ,
                    Permissions.CONSULTAS_READ,
                    Permissions.CONSULTAS_WRITE,
                    Permissions.EXAMES_BASE_READ,
                    Permissions.EXAMES_SOLICITADOS_READ,
                    Permissions.EXAMES_SOLICITADOS_WRITE,
                    Permissions.MEDICAMENTOS_BASE_READ,
                    Permissions.MEDICAMENTOS_READ,
                    Permissions.MEDICAMENTOS_WRITE
            );
            case SECRETARIA -> Set.of(
                    Permissions.PACIENTES_READ,
                    Permissions.PACIENTES_WRITE,
                    Permissions.MEDICOS_READ,
                    Permissions.CONSULTAS_READ,
                    Permissions.CONSULTAS_WRITE,
                    Permissions.CONSULTAS_DELETE,
                    Permissions.EXAMES_BASE_READ,
                    Permissions.EXAMES_SOLICITADOS_READ,
                    Permissions.MEDICAMENTOS_BASE_READ,
                    Permissions.MEDICAMENTOS_READ
            );
            case ATENDENTE -> Set.of(
                    Permissions.PACIENTES_READ,
                    Permissions.PACIENTES_WRITE,
                    Permissions.MEDICOS_READ,
                    Permissions.CONSULTAS_READ,
                    Permissions.CONSULTAS_WRITE,
                    Permissions.EXAMES_BASE_READ,
                    Permissions.EXAMES_SOLICITADOS_READ,
                    Permissions.MEDICAMENTOS_BASE_READ,
                    Permissions.MEDICAMENTOS_READ
            );
            case ENFERMEIRO -> Set.of(
                    Permissions.PACIENTES_READ,
                    Permissions.PACIENTES_WRITE,
                    Permissions.MEDICOS_READ,
                    Permissions.CONSULTAS_READ,
                    Permissions.EXAMES_BASE_READ,
                    Permissions.EXAMES_SOLICITADOS_READ,
                    Permissions.EXAMES_SOLICITADOS_WRITE,
                    Permissions.MEDICAMENTOS_BASE_READ,
                    Permissions.MEDICAMENTOS_READ
            );
            case PACIENTE -> Set.of();
        };
    }
}
