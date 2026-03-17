CREATE SEQUENCE IF NOT EXISTS consultas_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS exame_base_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS exame_solicitado_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS medicamento_base_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS medicamento_prescrito_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS pacientes_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS tb_role_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS tb_usuarios_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS pacientes (
    ativo boolean NOT NULL,
    data_nascimento date NOT NULL,
    id bigint NOT NULL DEFAULT nextval('pacientes_id_seq'),
    bairro varchar(255),
    cep varchar(255),
    cidade varchar(255),
    complemento varchar(255),
    cpf varchar(255),
    email varchar(255),
    logradouro varchar(255),
    nome varchar(255),
    numero varchar(255),
    plano_saude varchar(255),
    sexo varchar(255) NOT NULL,
    sobrenome varchar(255),
    telefone varchar(255),
    uf varchar(255),
    CONSTRAINT pacientes_pkey PRIMARY KEY (id),
    CONSTRAINT pacientes_cpf_key UNIQUE (cpf)
);

CREATE TABLE IF NOT EXISTS tb_usuarios (
    ativo boolean NOT NULL,
    id bigint NOT NULL DEFAULT nextval('tb_usuarios_id_seq'),
    bairro varchar(255),
    cep varchar(255),
    cidade varchar(255),
    complemento varchar(255),
    cpf varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    login varchar(255) NOT NULL,
    logradouro varchar(255),
    nome varchar(255) NOT NULL,
    numero varchar(255),
    senha varchar(255) NOT NULL,
    sobrenome varchar(255) NOT NULL,
    uf varchar(255),
    CONSTRAINT tb_usuarios_pkey PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT uk_usuario_cpf UNIQUE (cpf),
    CONSTRAINT uk_usuario_login UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS tb_role (
    id bigint NOT NULL DEFAULT nextval('tb_role_id_seq'),
    authority varchar(255),
    CONSTRAINT tb_role_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_medicos (
    data_formacao date,
    usuario_id bigint NOT NULL,
    crm varchar(255) NOT NULL,
    especialidade varchar(255),
    instituicao_formacao varchar(255),
    observacoes varchar(255),
    sexo varchar(255),
    CONSTRAINT tb_medicos_pkey PRIMARY KEY (usuario_id),
    CONSTRAINT tb_medicos_crm_key UNIQUE (crm)
);

CREATE TABLE IF NOT EXISTS enfermeiros (
    data_admissao date,
    data_demissao date,
    uf_coren varchar(2) NOT NULL,
    id bigint NOT NULL,
    especialidade_enfermagem varchar(120),
    observacoes varchar(1000),
    coren varchar(255) NOT NULL,
    setor_clinico varchar(255),
    turno_trabalho varchar(255),
    CONSTRAINT enfermeiros_pkey PRIMARY KEY (id),
    CONSTRAINT enfermeiros_coren_key UNIQUE (coren),
    CONSTRAINT enfermeiros_setor_clinico_check CHECK (setor_clinico IN ('AMBULATORIO', 'EMERGENCIA', 'UTI', 'CENTRO_CIRURGICO', 'ENFERMARIA', 'PEDIATRIA', 'GINECOLOGIA', 'OUTRO')),
    CONSTRAINT enfermeiros_turno_trabalho_check CHECK (turno_trabalho IN ('MANHA', 'TARDE', 'NOITE', 'INTEGRAL', 'PLANTAO'))
);

CREATE TABLE IF NOT EXISTS consultas (
    duracao_minutos integer,
    pago boolean,
    retorno boolean NOT NULL,
    teleconsulta boolean NOT NULL,
    valor_consulta double precision,
    data_hora timestamp(6) without time zone NOT NULL,
    data_limite_retorno timestamp(6) without time zone,
    data_pagamento timestamp(6) without time zone,
    id bigint NOT NULL DEFAULT nextval('consultas_id_seq'),
    medico_id bigint NOT NULL,
    paciente_id bigint NOT NULL,
    motivo varchar(500),
    observacoes varchar(2000),
    anamnese varchar(4000),
    diagnostico varchar(4000),
    exame_fisico varchar(4000),
    prescricao varchar(4000),
    link_acesso varchar(255),
    meio_pagamento varchar(255) NOT NULL,
    numero_carteirinha varchar(255),
    plano_saude varchar(255),
    status varchar(255) NOT NULL,
    tipo varchar(255) NOT NULL,
    CONSTRAINT consultas_pkey PRIMARY KEY (id),
    CONSTRAINT consultas_meio_pagamento_check CHECK (meio_pagamento IN ('DEBITO', 'CREDITO', 'PIX')),
    CONSTRAINT consultas_status_check CHECK (status IN ('AGENDADA', 'CONFIRMADA', 'EM_ATENDIMENTO', 'CONCLUIDA', 'CANCELADA')),
    CONSTRAINT consultas_tipo_check CHECK (tipo IN ('PRESENCIAL', 'TELECONSULTA', 'RETORNO', 'URGENCIA'))
);

CREATE TABLE IF NOT EXISTS exame_base (
    prazo_estimado integer NOT NULL,
    id bigint NOT NULL DEFAULT nextval('exame_base_id_seq'),
    codigo_tuss varchar(255),
    nome varchar(255),
    tipo varchar(255),
    CONSTRAINT exame_base_pkey PRIMARY KEY (id),
    CONSTRAINT exame_base_tipo_check CHECK (tipo IN ('LABORATORIAL', 'IMAGEM', 'OUTRO'))
);

CREATE TABLE IF NOT EXISTS exame_solicitado (
    consulta_id bigint,
    data_coleta timestamp(6) without time zone,
    data_resultado timestamp(6) without time zone,
    exame_base_id bigint,
    id bigint NOT NULL DEFAULT nextval('exame_solicitado_id_seq'),
    justificativa varchar(255),
    observacoes varchar(255),
    status varchar(255),
    CONSTRAINT exame_solicitado_pkey PRIMARY KEY (id),
    CONSTRAINT exame_solicitado_status_check CHECK (status IN ('SOLICITADO', 'AGENDADO', 'REALIZADO', 'CANCELADO'))
);

CREATE TABLE IF NOT EXISTS medicamento_base (
    id bigint NOT NULL DEFAULT nextval('medicamento_base_seq'),
    codigoatc varchar(255),
    dcb varchar(255),
    "dosagem_padrão" varchar(255),
    forma_farmaceutica varchar(255),
    nome_comercial varchar(255),
    pricipio_ativo varchar(255),
    via_administracao varchar(255),
    CONSTRAINT medicamento_base_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS medicamento_prescrito (
    consulta_id bigint,
    id bigint NOT NULL DEFAULT nextval('medicamento_prescrito_id_seq'),
    medicamento_base_id bigint,
    paciente_id bigint,
    dosagem varchar(255),
    frequencia varchar(255),
    nome varchar(255),
    via varchar(255),
    CONSTRAINT medicamento_prescrito_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_usuario_role (
    role_id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    CONSTRAINT tb_usuario_role_pkey PRIMARY KEY (role_id, usuario_id)
);

ALTER TABLE ONLY tb_medicos
    ADD CONSTRAINT fklm169t4fhtljov9mg79nuhbip FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(id);

ALTER TABLE ONLY enfermeiros
    ADD CONSTRAINT fkkd3vxabdsysoq2y1v10o6eouh FOREIGN KEY (id) REFERENCES tb_usuarios(id);

ALTER TABLE ONLY consultas
    ADD CONSTRAINT fk8rgyvij4segs1tlhycdyfiu2n FOREIGN KEY (medico_id) REFERENCES tb_medicos(usuario_id);

ALTER TABLE ONLY consultas
    ADD CONSTRAINT fke93cdauso5jd1y4bdpox45n4t FOREIGN KEY (paciente_id) REFERENCES pacientes(id);

ALTER TABLE ONLY exame_solicitado
    ADD CONSTRAINT fkhejwn1f1dlpx4ah6ncmxqehku FOREIGN KEY (consulta_id) REFERENCES consultas(id);

ALTER TABLE ONLY exame_solicitado
    ADD CONSTRAINT fknxoullrrftd4mbsp7sv0cljy8 FOREIGN KEY (exame_base_id) REFERENCES exame_base(id);

ALTER TABLE ONLY medicamento_prescrito
    ADD CONSTRAINT fkpqfk7osg86whf2qd0647wween FOREIGN KEY (consulta_id) REFERENCES consultas(id);

ALTER TABLE ONLY medicamento_prescrito
    ADD CONSTRAINT fkg5daca85m6u284puplm2e36ne FOREIGN KEY (medicamento_base_id) REFERENCES medicamento_base(id);

ALTER TABLE ONLY medicamento_prescrito
    ADD CONSTRAINT fksdnvtr5if90i5s1lq8sf16nsf FOREIGN KEY (paciente_id) REFERENCES pacientes(id);

ALTER TABLE ONLY tb_usuario_role
    ADD CONSTRAINT fkbrq9flkj5y52rn0idtyjliovr FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(id);

ALTER TABLE ONLY tb_usuario_role
    ADD CONSTRAINT fkkix4nwaehqjwnk40e2e36903j FOREIGN KEY (role_id) REFERENCES tb_role(id);