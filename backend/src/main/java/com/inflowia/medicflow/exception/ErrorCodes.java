package com.inflowia.medicflow.exception;

public final class ErrorCodes {

    public static final String CORE_REQUEST_INVALID = "CORE-REQUEST-400";
    public static final String CORE_REQUEST_BODY_INVALID = "CORE-VALIDATION-422";
    public static final String CORE_INTERNAL_ERROR = "CORE-INTERNAL-500";

    public static final String AUTH_UNAUTHORIZED_OPERATION = "AUTH-FORBIDDEN-403";
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH-CREDENTIALS-401";
    public static final String AUTH_ACCESS_DENIED = "AUTH-ACCESS-DENIED-403";
    public static final String AUTH_AUTHENTICATION_ERROR = "AUTH-AUTHENTICATION-401";

    public static final String PACIENTE_NOT_FOUND = "PACIENTE-NOT-FOUND-404";
    public static final String MEDICO_NOT_FOUND = "MEDICO-NOT-FOUND-404";
    public static final String USUARIO_NOT_FOUND = "USUARIO-NOT-FOUND-404";
    public static final String CONSULTA_NOT_FOUND = "CONSULTA-NOT-FOUND-404";
    public static final String CONSULTA_BUSINESS_RULE = "CONSULTA-BUSINESS-422";
    public static final String EXAME_BASE_NOT_FOUND = "EXAME-BASE-NOT-FOUND-404";
    public static final String EXAME_BASE_BUSINESS_RULE = "EXAME-BASE-BUSINESS-422";
    public static final String EXAME_SOLICITADO_NOT_FOUND = "EXAME-SOLICITADO-NOT-FOUND-404";
    public static final String EXAME_SOLICITADO_BUSINESS_RULE = "EXAME-SOLICITADO-BUSINESS-422";
    public static final String MEDICAMENTO_BASE_NOT_FOUND = "MEDICAMENTO-BASE-NOT-FOUND-404";
    public static final String MEDICAMENTO_NOT_FOUND = "MEDICAMENTO-NOT-FOUND-404";
    public static final String MEDICAMENTO_BUSINESS_RULE = "MEDICAMENTO-BUSINESS-422";
    public static final String ROLE_NOT_FOUND = "AUTH-ROLE-NOT-FOUND-404";

    private ErrorCodes() {
    }
}
