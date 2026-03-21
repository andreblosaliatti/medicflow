package com.inflowia.medicflow.domain.consulta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public enum TipoConsulta {
    PRESENCIAL("Presencial"),
    TELECONSULTA("Teleconsulta"),
    RETORNO("Retorno"),
    URGENCIA("Urgência");

    private final String label;
    private final Set<String> aliases;

    TipoConsulta(String label, String... aliases) {
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
    public static TipoConsulta fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equals(normalized) || tipo.aliases.contains(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de consulta inválido: " + value));
    }
}
