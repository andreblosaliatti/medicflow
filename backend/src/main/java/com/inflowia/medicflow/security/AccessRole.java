package com.inflowia.medicflow.security;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public enum AccessRole {

    ADMIN("ROLE_ADMIN", "ADMIN", Set.of("ROLE_ADMIN", "ADMIN")),
    MEDICO("ROLE_MEDICO", "MEDICO", Set.of("ROLE_MEDICO", "MEDICO")),
    SECRETARIA("ROLE_SECRETARIA", "SECRETARIA", Set.of(
            "ROLE_SECRETARIA",
            "ROLE_SECRETARIO",
            "SECRETARIA",
            "SECRETARIO"
    )),
    ATENDENTE("ROLE_ATENDENTE", "ATENDENTE", Set.of("ROLE_ATENDENTE", "ATENDENTE")),
    ENFERMEIRO("ROLE_ENFERMEIRO", "ENFERMEIRO", Set.of("ROLE_ENFERMEIRO", "ENFERMEIRO")),
    PACIENTE("ROLE_PACIENTE", "PACIENTE", Set.of("ROLE_PACIENTE", "PACIENTE"));

    private final String authority;
    private final String apiName;
    private final Set<String> acceptedValues;

    AccessRole(String authority, String apiName, Set<String> acceptedValues) {
        this.authority = authority;
        this.apiName = apiName;
        this.acceptedValues = acceptedValues;
    }

    public String authority() {
        return authority;
    }

    public String apiName() {
        return apiName;
    }

    public static AccessRole fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Perfil inválido: " + value);
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(role -> role.acceptedValues.contains(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Perfil inválido: " + value));
    }

    public static List<String> toApiNames(Iterable<String> authorities) {
        return stream(authorities)
                .map(AccessRole::fromValue)
                .map(AccessRole::apiName)
                .distinct()
                .sorted()
                .toList();
    }

    public static Set<String> toCanonicalAuthorities(Iterable<String> roles) {
        return stream(roles)
                .map(AccessRole::fromValue)
                .map(AccessRole::authority)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    private static java.util.stream.Stream<String> stream(Iterable<String> values) {
        if (values == null) {
            return java.util.stream.Stream.empty();
        }

        return java.util.stream.StreamSupport.stream(values.spliterator(), false)
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder());
    }
}
