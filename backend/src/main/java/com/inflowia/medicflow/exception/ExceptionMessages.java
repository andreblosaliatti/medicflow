package com.inflowia.medicflow.exception;

public final class ExceptionMessages {

    public static final String INVALID_DATA = "Dados inválidos.";
    public static final String INTERNAL_ERROR = "Erro interno inesperado.";
    public static final String INVALID_LOGIN = "Login ou senha inválidos.";
    public static final String INACTIVE_USER = "Usuário inativo.";
    public static final String AUTHENTICATED_USER_NOT_FOUND = "Usuário autenticado não encontrado.";
    public static final String AUTHENTICATION_REQUIRED = "Autenticação é obrigatória para acessar este recurso.";
    public static final String ACCESS_DENIED = "Você não tem permissão para realizar esta operação.";
    public static final String MEDICATION_INFO_REQUIRED = "Informe o medicamento pelo nome ou selecione um medicamento base.";
    public static final String DOSAGE_REQUIRED = "A dosagem do medicamento é obrigatória.";
    public static final String FREQUENCY_REQUIRED = "A frequência do medicamento é obrigatória.";
    public static final String ROUTE_REQUIRED = "A via de administração do medicamento é obrigatória.";
    public static final String NO_CONSULTATIONS_FOR_PATIENT = "Nenhuma consulta encontrada para o paciente informado.";
    public static final String NO_PATIENT_CONSULTATIONS = "O paciente informado não possui consultas.";
    public static final String TELECONSULTATION_LINK_REQUIRED = "Teleconsultas devem informar um link de acesso.";
    public static final String PAYMENT_DATE_REQUIRED = "Consultas pagas devem informar a data do pagamento.";
    public static final String PAYMENT_DATE_NOT_ALLOWED = "Consultas não pagas não devem informar data de pagamento.";
    public static final String PAID_CONSULTATION_INVALID_STATUS = "Consultas pagas só podem estar com status CONFIRMADA, EM_ATENDIMENTO ou CONCLUIDA.";
    public static final String CANCELED_CONSULTATION_PRESCRIPTION_NOT_ALLOWED = "Consultas canceladas não podem receber prescrição.";
    public static final String CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED = "Não é possível adicionar medicamentos a uma consulta cancelada.";

    private ExceptionMessages() {
    }

    public static String notFound(String resourceName) {
        return resourceName + " não encontrado.";
    }

    public static String notFoundBy(String resourceName, String attributeName, String attributeValue) {
        return resourceName + " não encontrado para " + attributeName + ": " + attributeValue + ".";
    }
}
