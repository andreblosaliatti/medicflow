package com.inflowia.medicflow.domain.consulta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public enum StatusConsulta {
    AGENDADA("Agendada"),
    CONFIRMADA("Confirmada"),
    EM_ATENDIMENTO("Em atendimento", "EM_ANDAMENTO"),
    CONCLUIDA("Concluída", "REALIZADA"),
    CANCELADA("Cancelada"),
    NAO_COMPARECEU("Não compareceu");

    private final String label;
    private final Set<String> aliases;

    StatusConsulta(String label, String... aliases) {
        this.label = label;
        this.aliases = Set.of(aliases);
    }

    @JsonValue
    public String getCode() {
        return name();
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static StatusConsulta fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(status -> status.name().equals(normalized) || status.aliases.contains(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status de consulta inválido: " + value));
    }
}
