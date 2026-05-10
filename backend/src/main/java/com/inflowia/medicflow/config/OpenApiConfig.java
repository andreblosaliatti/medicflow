package com.inflowia.medicflow.config;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.dto.CustomError;
import com.inflowia.medicflow.dto.ValidationError;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.PathItem;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medicflowOpenApi() {
        Components components = new Components()
                .addSchemas("FieldMessage", fieldMessageSchema())
                .addSchemas("ApiError", apiErrorSchema())
                .addSchemas("ValidationError", validationErrorSchema())
                .addResponses("BadRequestError", createValidationResponse(
                        "400 - Requisição inválida. Use este retorno para erro de serialização, tipo incompatível, query/path/header malformado ou validação de parâmetros.",
                        "CORE-REQUEST-400",
                        "status",
                        "must not be null"
                ))
                .addResponses("UnauthorizedError", createErrorResponse(
                        "401 - Falha de autenticação.",
                        "AUTH-AUTHENTICATION-401",
                        "Autenticação é obrigatória para acessar este recurso."
                ))
                .addResponses("ForbiddenError", createErrorResponse(
                        "403 - Usuário autenticado sem permissão para a operação.",
                        "AUTH-ACCESS-DENIED-403",
                        "Você não tem permissão para realizar esta operação."
                ))
                .addResponses("NotFoundError", createErrorResponse(
                        "404 - Recurso de domínio não encontrado.",
                        "PACIENTE-NOT-FOUND-404",
                        "Paciente não encontrado."
                ))
                .addResponses("UnprocessableEntityError", createValidationResponse(
                        "422 - Conteúdo semanticamente inválido. Use este retorno para violações do contrato do corpo JSON e regras de validação do formulário.",
                        "CORE-VALIDATION-422",
                        "cpf",
                        "CPF é obrigatório"
                ))
                .addResponses("InternalServerError", createErrorResponse(
                        "500 - Erro inesperado no servidor.",
                        "CORE-INTERNAL-500",
                        "Erro interno inesperado."
                ));

        return new OpenAPI().components(components);
    }

    private Schema<CustomError> apiErrorSchema() {
        return new Schema<CustomError>()
                .type("object")
                .addProperty("timestamp", new DateTimeSchema().example("2026-03-21T12:00:00Z"))
                .addProperty("status", new IntegerSchema().example(404))
                .addProperty("error", new StringSchema().example("Not Found"))
                .addProperty("code", new StringSchema().example("PACIENTE-NOT-FOUND-404"))
                .addProperty("message", new StringSchema().example("Paciente não encontrado."))
                .addProperty("path", new StringSchema().example(ApiPaths.PACIENTES + "/999"))
                .addProperty("traceId", new StringSchema().example("2ab7d2ca-2ca2-4b67-940d-f7d8c19860ef"));
    }

    private Schema<ValidationError> validationErrorSchema() {
        return new Schema<ValidationError>()
                .type("object")
                .addProperty("timestamp", new DateTimeSchema().example("2026-03-21T12:00:00Z"))
                .addProperty("status", new IntegerSchema().example(422))
                .addProperty("error", new StringSchema().example("Unprocessable Entity"))
                .addProperty("code", new StringSchema().example("CORE-VALIDATION-422"))
                .addProperty("message", new StringSchema().example("Dados inválidos."))
                .addProperty("path", new StringSchema().example(ApiPaths.PACIENTES))
                .addProperty("traceId", new StringSchema().example("2ab7d2ca-2ca2-4b67-940d-f7d8c19860ef"))
                .addProperty("errors", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/FieldMessage")));
    }

    private Schema<?> fieldMessageSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("fieldName", new StringSchema().example("cpf"))
                .addProperty("message", new StringSchema().example("CPF é obrigatório"));
    }

    @Bean
    public OpenApiCustomizer medicflowContractResponsesCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> pathItem.readOperationsMap().forEach((method, operation) -> {
                ApiResponses responses = operation.getResponses() != null ? operation.getResponses() : new ApiResponses();
                addRefIfMissing(responses, "400", "#/components/responses/BadRequestError");
                addRefIfMissing(responses, "401", "#/components/responses/UnauthorizedError");
                addRefIfMissing(responses, "403", "#/components/responses/ForbiddenError");
                addRefIfMissing(responses, "500", "#/components/responses/InternalServerError");

                if (path.contains("{")) {
                    addRefIfMissing(responses, "404", "#/components/responses/NotFoundError");
                }

                if (method == PathItem.HttpMethod.POST || method == PathItem.HttpMethod.PUT || method == PathItem.HttpMethod.PATCH) {
                    addRefIfMissing(responses, "422", "#/components/responses/UnprocessableEntityError");
                }

                operation.setResponses(responses);
            }));
        };
    }

    private ApiResponse createErrorResponse(String description, String code, String message) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(
                        "application/json",
                        new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ApiError"))
                                .examples(Map.of("default", new Example().value(Map.of(
                                        "timestamp", "2026-03-21T12:00:00Z",
                                        "status", 404,
                                        "error", "Not Found",
                                        "code", code,
                                        "message", message,
                                        "path", ApiPaths.API_V1 + "/recurso/999",
                                        "traceId", "2ab7d2ca-2ca2-4b67-940d-f7d8c19860ef"
                                ))))
                ));
    }

    private ApiResponse createValidationResponse(String description, String code, String fieldName, String fieldMessage) {
        Map<String, Object> exampleBody = new LinkedHashMap<>();
        exampleBody.put("timestamp", "2026-03-21T12:00:00Z");
        exampleBody.put("status", code.endsWith("422") ? 422 : 400);
        exampleBody.put("error", code.endsWith("422") ? "Unprocessable Entity" : "Bad Request");
        exampleBody.put("code", code);
        exampleBody.put("message", "Dados inválidos.");
        exampleBody.put("path", ApiPaths.API_V1 + "/recurso");
        exampleBody.put("traceId", "2ab7d2ca-2ca2-4b67-940d-f7d8c19860ef");
        exampleBody.put("errors", new Object[]{Map.of("fieldName", fieldName, "message", fieldMessage)});

        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(
                        "application/json",
                        new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ValidationError"))
                                .examples(Map.of("default", new Example().value(exampleBody)))
                ));
    }

    private void addRefIfMissing(ApiResponses responses, String status, String ref) {
        if (!responses.containsKey(status)) {
            responses.addApiResponse(status, new ApiResponse().$ref(ref));
        }
    }
}
