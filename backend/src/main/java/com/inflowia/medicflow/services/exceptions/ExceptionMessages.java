package com.inflowia.medicflow.services.exceptions;

public final class ExceptionMessages {

    public static final String INVALID_DATA = "Dados inválidos.";
    public static final String INTERNAL_ERROR = "Erro interno inesperado.";
    public static final String INVALID_LOGIN = "Login ou senha inválidos.";
    public static final String INACTIVE_USER = "Usuário inativo.";
    public static final String AUTHENTICATED_USER_NOT_FOUND = "Usuário autenticado não encontrado.";
    public static final String ACCESS_DENIED = "Você não tem permissão para realizar esta operação.";
    public static final String MEDICATION_INFO_REQUIRED = "Informe o medicamento pelo nome ou selecione um medicamento base.";
    public static final String DOSAGE_REQUIRED = "A dosagem do medicamento é obrigatória.";
    public static final String FREQUENCY_REQUIRED = "A frequência do medicamento é obrigatória.";
    public static final String ROUTE_REQUIRED = "A via de administração do medicamento é obrigatória.";
    public static final String NO_CONSULTATIONS_FOR_PATIENT = "Nenhuma consulta encontrada para o paciente informado.";
    public static final String NO_PATIENT_CONSULTATIONS = "O paciente informado não possui consultas.";

    private ExceptionMessages() {
    }

    public static String notFound(String resourceName) {
        return resourceName + " não encontrado.";
    }

    public static String notFoundBy(String resourceName, String attributeName, String attributeValue) {
        return resourceName + " não encontrado para " + attributeName + ": " + attributeValue + ".";
    }
}
