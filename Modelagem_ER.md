erDiagram
  USUARIOS ||--o{ MEDICOS : "1-1 (JOINED via PK)"
  PACIENTES ||--o{ CONSULTAS : "1-N"
  MEDICOS ||--o{ CONSULTAS : "1-N"
  CONSULTAS ||--o{ MEDICAMENTOS_PRESCRITOS : "1-N"
  MEDICAMENTOS_BASE ||--o{ MEDICAMENTOS_PRESCRITOS : "1-N"

  PACIENTES ||--o{ PEDIDOS_EXAME : "1-N"
  CONSULTAS ||--o{ PEDIDOS_EXAME : "0..1-N (opcional)"
  MEDICOS ||--o{ PEDIDOS_EXAME : "1-N (solicitante)"

  PEDIDOS_EXAME ||--o{ PEDIDO_EXAME_ITENS : "1-N"
  EXAME_CATALOGO ||--o{ PEDIDO_EXAME_ITENS : "1-N"
  AMOSTRAS ||--o{ PEDIDO_EXAME_ITENS : "1-N (compartilhável)"

  PEDIDO_EXAME_ITENS ||--o{ RESULTADOS_EXAME : "1-N"
  RESULTADOS_EXAME ||--o{ ARQUIVOS_ANEXO : "1-N"

  EXAME_CATALOGO ||--o{ EXAME_COMPONENTE : "1-N (painel → filhos)"
  EXAME_CATALOGO ||--o{ EXAME_COMPONENTE : "1-N (filho ← painel)"

  USUARIOS {
    bigserial id PK
    varchar login UK
    varchar senha
    varchar nome
    varchar sobrenome
    varchar email UK
    varchar cpf UK
    varchar telefone
    varchar logradouro
    varchar numero
    varchar complemento
    varchar bairro
    varchar cidade
    varchar uf
    varchar cep
    perfil_enum perfil
    boolean ativo
    timestamptz criado_em
    timestamptz atualizado_em
  }

  MEDICOS {
    bigint usuario_id PK,FK->USUARIOS.id
    varchar crm
    varchar uf_crm
    varchar especialidade
  }

  PACIENTES {
    bigserial id PK
    varchar nome
    varchar cpf UK
    date data_nascimento
    varchar telefone
    varchar email UK
    varchar logradouro
    varchar numero
    varchar complemento
    varchar bairro
    varchar cidade
    varchar uf
    varchar cep
    varchar plano_saude
    boolean ativo
    timestamptz criado_em
    timestamptz atualizado_em
  }

  CONSULTAS {
    bigserial id PK
    bigint paciente_id FK->PACIENTES.id
    bigint medico_id FK->MEDICOS.usuario_id
    timestamptz data_hora
    tipo_consulta_enum tipo
    status_consulta_enum status
    text motivo
    text anamnese
    text exame_fisico
    text diagnostico
    text prescricao_texto
    text observacoes
    text laudo
    timestamptz criado_em
  }

  MEDICAMENTOS_BASE {
    bigserial id PK
    varchar dcb
    varchar nome_comercial
    varchar principio_ativo
    varchar forma_farmaceutica
    varchar dosagem
    varchar via_administracao
    varchar codigo_atc
  }

  MEDICAMENTOS_PRESCRITOS {
    bigserial id PK
    bigint consulta_id FK->CONSULTAS.id
    bigint paciente_id FK->PACIENTES.id
    bigint medicamento_base_id FK->MEDICAMENTOS_BASE.id
    varchar dose
    varchar frequencia
    varchar duracao
    varchar via
    text observacoes
  }

  EXAME_CATALOGO {
    bigserial id PK
    varchar codigo_loinc
    varchar nome
    varchar tipo  "LAB|IMAGEM"
    varchar amostra_padrao
    varchar unidade_padrao
    boolean painel
  }

  EXAME_COMPONENTE {
    bigserial id PK
    bigint exame_painel_id FK->EXAME_CATALOGO.id
    bigint exame_filho_id FK->EXAME_CATALOGO.id
  }

  PEDIDOS_EXAME {
    bigserial id PK
    bigint paciente_id FK->PACIENTES.id
    bigint consulta_id FK->CONSULTAS.id
    bigint medico_id FK->MEDICOS.usuario_id
    timestamptz data_pedido
    varchar prioridade
    status_pedido_exame_enum status
  }

  AMOSTRAS {
    bigserial id PK
    varchar tipo
    varchar codigo_barras UK
    timestamptz data_coleta
    bigint coletado_por FK->USUARIOS.id
    varchar local_coleta
    varchar status
    varchar volume
  }

  PEDIDO_EXAME_ITENS {
    bigserial id PK
    bigint pedido_id FK->PEDIDOS_EXAME.id
    bigint exame_id FK->EXAME_CATALOGO.id
    bigint amostra_id FK->AMOSTRAS.id
    status_item_exame_enum status_item
    text observacoes
  }

  RESULTADOS_EXAME {
    bigserial id PK
    bigint item_id FK->PEDIDO_EXAME_ITENS.id
    numeric valor_num
    varchar valor_texto
    varchar unidade
    numeric ref_min
    numeric ref_max
    varchar ref_texto
    varchar metodo
    timestamptz data_resultado
    status_validacao_resultado_enum status_validacao
    bigint validado_por FK->USUARIOS.id
    text interpretacao
  }

  ARQUIVOS_ANEXO {
    bigserial id PK
    varchar origem "RESULTADO|PEDIDO"
    bigint origem_id
    varchar nome
    varchar mime_type
    bigint tamanho
    varchar url_armazenamento
    timestamptz criado_em
  }


-- ===== Enums =====
CREATE TYPE perfil_enum AS ENUM ('ADMIN','MEDICO','SECRETARIO','ATENDENTE','ENFERMEIRO','PACIENTE');
CREATE TYPE tipo_consulta_enum AS ENUM ('PRESENCIAL','TELECONSULTA','RETORNO','URGENCIA');
CREATE TYPE status_consulta_enum AS ENUM ('AGENDADA','EM_ANDAMENTO','REALIZADA','CANCELADA','NAO_COMPARECEU');
CREATE TYPE status_pedido_exame_enum AS ENUM ('SOLICITADO','COLETADO','EM_ANALISE','PRONTO','CANCELADO');
CREATE TYPE status_item_exame_enum AS ENUM ('SOLICITADO','COLETADO','EM_ANALISE','PRONTO','CANCELADO');
CREATE TYPE status_validacao_resultado_enum AS ENUM ('PRELIMINAR','VALIDADO');

-- ===== Usuários / Médicos =====
CREATE TABLE usuarios (
  id               BIGSERIAL PRIMARY KEY,
  login            VARCHAR(80)  NOT NULL UNIQUE,
  senha            VARCHAR(120) NOT NULL,
  nome             VARCHAR(120) NOT NULL,
  sobrenome        VARCHAR(120),
  email            VARCHAR(160) NOT NULL UNIQUE,
  cpf              VARCHAR(14)  NOT NULL UNIQUE,
  telefone         VARCHAR(20),

  -- Endereço embutido
  logradouro       VARCHAR(160),
  numero           VARCHAR(20),
  complemento      VARCHAR(60),
  bairro           VARCHAR(80),
  cidade           VARCHAR(80),
  uf               VARCHAR(2),
  cep              VARCHAR(9),

  perfil           perfil_enum  NOT NULL,
  ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
  criado_em        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  atualizado_em    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE medicos (
  usuario_id       BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
  crm              VARCHAR(20) NOT NULL,
  uf_crm           VARCHAR(2)  NOT NULL,
  especialidade    VARCHAR(120)
);

-- ===== Pacientes =====
CREATE TABLE pacientes (
  id               BIGSERIAL PRIMARY KEY,
  nome             VARCHAR(160) NOT NULL,
  cpf              VARCHAR(14)  NOT NULL UNIQUE,
  data_nascimento  DATE        NOT NULL,
  telefone         VARCHAR(20),
  email            VARCHAR(160) UNIQUE,

  -- Endereço embutido
  logradouro       VARCHAR(160),
  numero           VARCHAR(20),
  complemento      VARCHAR(60),
  bairro           VARCHAR(80),
  cidade           VARCHAR(80),
  uf               VARCHAR(2),
  cep              VARCHAR(9),

  plano_saude      VARCHAR(120),
  ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
  criado_em        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  atualizado_em    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ===== Consultas =====
CREATE TABLE consultas (
  id               BIGSERIAL PRIMARY KEY,
  paciente_id      BIGINT      NOT NULL REFERENCES pacientes(id),
  medico_id        BIGINT      NOT NULL REFERENCES medicos(usuario_id),
  data_hora        TIMESTAMPTZ NOT NULL,
  tipo             tipo_consulta_enum   NOT NULL,
  status           status_consulta_enum NOT NULL DEFAULT 'AGENDADA',
  motivo           TEXT,
  anamnese         TEXT,
  exame_fisico     TEXT,
  diagnostico      TEXT,
  prescricao_texto TEXT,
  observacoes      TEXT,
  laudo            TEXT,
  criado_em        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


Consultas sql

DDL PostgreSQL (pronto para rodar)

Copie tudo e execute (ajuste schema/database se quiser). Mantém endereço “embutido” em USUARIOS/PACIENTES como no seu JPA.



CREATE INDEX idx_consultas_paciente_data ON consultas(paciente_id, data_hora);
CREATE INDEX idx_consultas_medico_data   ON consultas(medico_id, data_hora);

-- ===== Medicamentos =====
CREATE TABLE medicamentos_base (
  id                 BIGSERIAL PRIMARY KEY,
  dcb                VARCHAR(80),
  nome_comercial     VARCHAR(160),
  principio_ativo    VARCHAR(160),
  forma_farmaceutica VARCHAR(80),
  dosagem            VARCHAR(80),
  via_administracao  VARCHAR(80),
  codigo_atc         VARCHAR(20)
);

CREATE TABLE medicamentos_prescritos (
  id                   BIGSERIAL PRIMARY KEY,
  consulta_id          BIGINT NOT NULL REFERENCES consultas(id) ON DELETE CASCADE,
  paciente_id          BIGINT NOT NULL REFERENCES pacientes(id),
  medicamento_base_id  BIGINT NOT NULL REFERENCES medicamentos_base(id),
  dose                 VARCHAR(80),
  frequencia           VARCHAR(80),
  duracao              VARCHAR(80),
  via                  VARCHAR(80),
  observacoes          TEXT
);

CREATE INDEX idx_rx_consulta ON medicamentos_prescritos(consulta_id);

-- ===== Exames: Catálogo + Painéis =====
CREATE TABLE exame_catalogo (
  id               BIGSERIAL PRIMARY KEY,
  codigo_loinc     VARCHAR(40),
  nome             VARCHAR(160) NOT NULL,
  tipo             VARCHAR(10)  NOT NULL CHECK (tipo IN ('LAB','IMAGEM')),
  amostra_padrao   VARCHAR(60),
  unidade_padrao   VARCHAR(40),
  painel           BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE exame_componente (
  id               BIGSERIAL PRIMARY KEY,
  exame_painel_id  BIGINT NOT NULL REFERENCES exame_catalogo(id) ON DELETE CASCADE,
  exame_filho_id   BIGINT NOT NULL REFERENCES exame_catalogo(id) ON DELETE CASCADE,
  UNIQUE(exame_painel_id, exame_filho_id)
);

-- ===== Exames: Pedidos / Itens / Amostras / Resultados =====
CREATE TABLE pedidos_exame (
  id               BIGSERIAL PRIMARY KEY,
  paciente_id      BIGINT NOT NULL REFERENCES pacientes(id),
  consulta_id      BIGINT REFERENCES consultas(id),
  medico_id        BIGINT NOT NULL REFERENCES medicos(usuario_id),
  data_pedido      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  prioridade       VARCHAR(20),
  status           status_pedido_exame_enum NOT NULL DEFAULT 'SOLICITADO'
);

CREATE INDEX idx_pedidos_exame_paciente ON pedidos_exame(paciente_id, data_pedido);

CREATE TABLE amostras (
  id               BIGSERIAL PRIMARY KEY,
  tipo             VARCHAR(40) NOT NULL,
  codigo_barras    VARCHAR(64) UNIQUE,
  data_coleta      TIMESTAMPTZ,
  coletado_por     BIGINT REFERENCES usuarios(id),
  local_coleta     VARCHAR(80),
  status           VARCHAR(30),
  volume           VARCHAR(30)
);

CREATE TABLE pedido_exame_itens (
  id               BIGSERIAL PRIMARY KEY,
  pedido_id        BIGINT NOT NULL REFERENCES pedidos_exame(id) ON DELETE CASCADE,
  exame_id         BIGINT NOT NULL REFERENCES exame_catalogo(id),
  amostra_id       BIGINT REFERENCES amostras(id),
  status_item      status_item_exame_enum NOT NULL DEFAULT 'SOLICITADO',
  observacoes      TEXT
);

CREATE INDEX idx_itens_pedido ON pedido_exame_itens(pedido_id);
CREATE INDEX idx_itens_amostra ON pedido_exame_itens(amostra_id);

CREATE TABLE resultados_exame (
  id                 BIGSERIAL PRIMARY KEY,
  item_id            BIGINT NOT NULL REFERENCES pedido_exame_itens(id) ON DELETE CASCADE,
  valor_num          NUMERIC,
  valor_texto        VARCHAR(160),
  unidade            VARCHAR(40),
  ref_min            NUMERIC,
  ref_max            NUMERIC,
  ref_texto          VARCHAR(160),
  metodo             VARCHAR(80),
  data_resultado     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  status_validacao   status_validacao_resultado_enum NOT NULL DEFAULT 'PRELIMINAR',
  validado_por       BIGINT REFERENCES usuarios(id),
  interpretacao      TEXT
);

CREATE INDEX idx_resultados_item ON resultados_exame(item_id);

-- ===== Anexos (PDF/DICOM/Imagens) =====
CREATE TABLE arquivos_anexo (
  id                 BIGSERIAL PRIMARY KEY,
  origem             VARCHAR(20) NOT NULL CHECK (origem IN ('RESULTADO','PEDIDO')),
  origem_id          BIGINT NOT NULL,
  nome               VARCHAR(200) NOT NULL,
  mime_type          VARCHAR(100),
  tamanho            BIGINT,
  url_armazenamento  VARCHAR(500) NOT NULL,
  criado_em          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- (Opcional) Índices úteis:
CREATE INDEX idx_anexo_origem ON arquivos_anexo(origem, origem_id);
