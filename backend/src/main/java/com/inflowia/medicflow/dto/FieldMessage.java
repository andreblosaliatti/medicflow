package com.inflowia.medicflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FieldMessage", description = "Detalhe de um campo inválido no contrato da API.")
public class FieldMessage {

    @Schema(example = "cpf")
    private String fieldName;
    @Schema(example = "CPF é obrigatório")
    private String message;

    public FieldMessage(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }
}
