CREATE TABLE IF NOT EXISTS tb_role (
    id BIGSERIAL PRIMARY KEY,
    authority VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_usuarios (
    id BIGSERIAL PRIMARY KEY,
    ativo BOOLEAN NOT NULL,
    cpf VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    login VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    sobrenome VARCHAR(255) NOT NULL,
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(255),
    cep VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_usuario_role (
    usuario_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    CONSTRAINT fk_usuario_role_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(id),
    CONSTRAINT fk_usuario_role_role FOREIGN KEY (role_id) REFERENCES tb_role(id)
);

CREATE TABLE IF NOT EXISTS pacientes (
    id BIGSERIAL PRIMARY KEY,
    ativo BOOLEAN,
    data_nascimento DATE,
    nome VARCHAR(255),
    sobrenome VARCHAR(255),
    cpf VARCHAR(255) UNIQUE,
    telefone VARCHAR(255),
    email VARCHAR(255),
    sexo VARCHAR(255),
    plano_saude VARCHAR(255),
    logradouro VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(255),
    cep VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_medicos (
    usuario_id BIGINT PRIMARY KEY,
    crm VARCHAR(255) UNIQUE NOT NULL,
    especialidade VARCHAR(255),
    instituicao_formacao VARCHAR(255),
    data_formacao DATE,
    sexo VARCHAR(255),
    observacoes VARCHAR(255),
    CONSTRAINT fk_medico_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(id)
);


CREATE TABLE IF NOT EXISTS enfermeiros (
    id BIGINT PRIMARY KEY,
    coren VARCHAR(255) UNIQUE NOT NULL,
    uf_coren VARCHAR(2) NOT NULL,
    especialidade_enfermagem VARCHAR(120),
    setor_clinico VARCHAR(255),
    turno_trabalho VARCHAR(255),
    data_admissao DATE,
    data_demissao DATE,
    observacoes VARCHAR(1000),
    CONSTRAINT fk_enfermeiro_usuario FOREIGN KEY (id) REFERENCES tb_usuarios(id)
);

CREATE TABLE IF NOT EXISTS consultas (
    id BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    valor_consulta DOUBLE PRECISION,
    meio_pagamento VARCHAR(255) NOT NULL,
    pago BOOLEAN,
    data_pagamento TIMESTAMP,
    duracao_minutos INTEGER,
    retorno BOOLEAN NOT NULL,
    data_limite_retorno TIMESTAMP,
    teleconsulta BOOLEAN NOT NULL,
    link_acesso VARCHAR(255),
    plano_saude VARCHAR(255),
    numero_carteirinha VARCHAR(255),
    motivo VARCHAR(500),
    anamnese VARCHAR(4000),
    exame_fisico VARCHAR(4000),
    diagnostico VARCHAR(4000),
    prescricao VARCHAR(4000),
    observacoes VARCHAR(2000),
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    CONSTRAINT fk_consulta_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    CONSTRAINT fk_consulta_medico FOREIGN KEY (medico_id) REFERENCES tb_medicos(usuario_id)
);

CREATE TABLE IF NOT EXISTS medicamento_base (
    id BIGSERIAL PRIMARY KEY,
    dcb VARCHAR(255),
    nome_comercial VARCHAR(255),
    forma_farmaceutica VARCHAR(255),
    concentracao VARCHAR(255),
    via_padrao VARCHAR(255),
    controlado BOOLEAN,
    ativo BOOLEAN
);

CREATE TABLE IF NOT EXISTS medicamento_prescrito (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    dosagem VARCHAR(255),
    frequencia VARCHAR(255),
    via VARCHAR(255),
    paciente_id BIGINT,
    consulta_id BIGINT,
    medicamento_base_id BIGINT,
    CONSTRAINT fk_med_presc_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    CONSTRAINT fk_med_presc_consulta FOREIGN KEY (consulta_id) REFERENCES consultas(id),
    CONSTRAINT fk_med_presc_base FOREIGN KEY (medicamento_base_id) REFERENCES medicamento_base(id)
);

CREATE TABLE IF NOT EXISTS exame_base (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    codigo_tuss VARCHAR(255),
    tipo VARCHAR(255),
    prazo_estimado INTEGER
);

CREATE TABLE IF NOT EXISTS exame_solicitado (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(255),
    justificativa VARCHAR(255),
    observacoes VARCHAR(255),
    data_coleta TIMESTAMP,
    data_resultado TIMESTAMP,
    consulta_id BIGINT,
    exame_base_id BIGINT,
    CONSTRAINT fk_exame_sol_consulta FOREIGN KEY (consulta_id) REFERENCES consultas(id),
    CONSTRAINT fk_exame_sol_base FOREIGN KEY (exame_base_id) REFERENCES exame_base(id)
);
