package com.inflowia.medicflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "ApiError",
        description = "Erro padrão do contrato da API. O campo code é estável para integrações e segue o padrão DOMINIO-TIPO-HTTP."
)
public class CustomError {

    @Schema(example = "2026-03-21T12:00:00Z")
    private final Instant timestamp;
    @Schema(example = "404")
    private final Integer status;
    @Schema(example = "Not Found")
    private final String error;
    @Schema(example = "PACIENTE-NOT-FOUND-404")
    private final String code;
    @Schema(example = "Paciente não encontrado.")
    private final String message;
    @Schema(example = "/pacientes/999")
    private final String path;
    @Schema(example = "2ab7d2ca-2ca2-4b67-940d-f7d8c19860ef", nullable = true)
    private final String traceId;

    public CustomError(Instant timestamp,
                       Integer status,
                       String error,
                       String code,
                       String message,
                       String path,
                       String traceId) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.traceId = traceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getTraceId() {
        return traceId;
    }
}
