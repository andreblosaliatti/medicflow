package com.inflowia.medicflow.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "ValidationError",
        description = "Erro de validação da API. Use 400 para parâmetros/path/query/header malformados e 422 para violações do payload JSON enviado no corpo."
)
public class ValidationError extends CustomError {

    @ArraySchema(schema = @Schema(implementation = FieldMessage.class))
    private final List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp,
                           Integer status,
                           String error,
                           String code,
                           String message,
                           String path,
                           String traceId) {
        super(timestamp, status, error, code, message, path, traceId);
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.removeIf(x -> x.getFieldName().equals(fieldName));
        errors.add(new FieldMessage(fieldName, message));
    }
}
