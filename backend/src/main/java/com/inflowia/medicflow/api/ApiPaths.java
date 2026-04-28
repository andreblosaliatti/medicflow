package com.inflowia.medicflow.api;

public final class ApiPaths {

    public static final String API_V1 = "/api/v1";

    public static final String AUTH = API_V1 + "/auth";
    public static final String CONSULTAS = API_V1 + "/consultas";
    public static final String PACIENTES = API_V1 + "/pacientes";
    public static final String MEDICOS = API_V1 + "/medicos";
    public static final String USUARIOS = API_V1 + "/usuarios";
    public static final String EXAMES_BASE = API_V1 + "/exames-base";
    public static final String EXAMES_SOLICITADOS = API_V1 + "/exames-solicitados";
    public static final String MEDICAMENTOS = API_V1 + "/medicamentos";
    public static final String MEDICAMENTOS_BASE = API_V1 + "/medicamentos-base";

    private ApiPaths() {
    }
}
