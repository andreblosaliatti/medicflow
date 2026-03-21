package com.inflowia.medicflow.security;

public final class Permissions {

    private Permissions() {
    }

    public static final String USUARIOS_READ = "usuarios:read";
    public static final String USUARIOS_WRITE = "usuarios:write";

    public static final String PACIENTES_READ = "pacientes:read";
    public static final String PACIENTES_WRITE = "pacientes:write";

    public static final String MEDICOS_READ = "medicos:read";
    public static final String MEDICOS_WRITE = "medicos:write";

    public static final String CONSULTAS_READ = "consultas:read";
    public static final String CONSULTAS_WRITE = "consultas:write";
    public static final String CONSULTAS_DELETE = "consultas:delete";

    public static final String EXAMES_BASE_READ = "exames-base:read";
    public static final String EXAMES_BASE_WRITE = "exames-base:write";

    public static final String EXAMES_SOLICITADOS_READ = "exames-solicitados:read";
    public static final String EXAMES_SOLICITADOS_WRITE = "exames-solicitados:write";

    public static final String MEDICAMENTOS_BASE_READ = "medicamentos-base:read";

    public static final String MEDICAMENTOS_READ = "medicamentos:read";
    public static final String MEDICAMENTOS_WRITE = "medicamentos:write";
}
