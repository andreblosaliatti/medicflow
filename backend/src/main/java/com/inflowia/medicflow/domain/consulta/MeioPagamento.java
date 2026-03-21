package com.inflowia.medicflow.domain.consulta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public enum MeioPagamento {
    DEBITO("Débito", "DEBIT", "CARTAO_DEBITO"),
    CREDITO("Crédito", "CREDIT", "CARTAO_CREDITO"),
    PIX("Pix"),
    DINHEIRO("Dinheiro", "CASH");

    private final String label;
    private final Set<String> aliases;

    MeioPagamento(String label, String... aliases) {
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
    public static MeioPagamento fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(meio -> meio.name().equals(normalized) || meio.aliases.contains(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Meio de pagamento inválido: " + value));
    }
}
