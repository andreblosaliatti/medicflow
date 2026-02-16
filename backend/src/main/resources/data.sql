INSERT INTO pacientes
(ativo, data_nascimento, nome, sobrenome, cpf, telefone, email, logradouro, numero, complemento, bairro, cidade, uf, cep, plano_saude, sexo)
VALUES
(TRUE, DATE '1985-04-12', 'Carlos',  'Santos',   '11144477735', '(51) 99888-1234', 'carlos.santos@example.com',  'Rua das Flores', '120', 'Apto 301', 'Centro',            'Porto Alegre', 'RS', '90560-001', 'Unimed',     'M'),
(TRUE, DATE '1992-11-23', 'Mariana', 'Costa',    '22255588846', '(51) 98444-5678', 'mariana.costa@example.com', 'Av. Ipiranga',   '4500', NULL,      'Azenha',            'Porto Alegre', 'RS', '90610-000', 'Cassi',      'F'),
(TRUE, DATE '1978-06-05', 'Ricardo', 'Almeida',  '33366699957', '(51) 99777-2222', 'ricardo.almeida@example.com','Rua da Praia',  '55',  'Casa 2',   'Centro Histórico',  'Porto Alegre', 'RS', '90010-000', 'Particular', 'M'),
(TRUE, DATE '1990-01-15', 'Ana',     'Silva',    '44477700018', '(51) 99111-0001', 'ana.silva@example.com',     'Rua A',          '10',  NULL,      'Bela Vista',        'Porto Alegre', 'RS', '90460-040', 'Unimed',     'F'),
(TRUE, DATE '1987-03-22', 'Bruno',   'Teixeira', '55588811129', '(51) 99222-0002', 'bruno.teixeira@example.com','Rua B',          '20',  NULL,      'Rio Branco',        'Porto Alegre', 'RS', '90420-050', 'Cassi',      'M'),
(TRUE, DATE '1995-07-09', 'Camila',  'Rocha',    '66699922230', '(51) 99333-0003', 'camila.rocha@example.com',  'Rua C',          '30',  'Sala 5',  'Moinhos de Vento', 'Porto Alegre', 'RS', '90570-060', 'Particular', 'F'),
(TRUE, DATE '1982-11-30', 'Diego',   'Pereira',  '77700033341', '(51) 99444-0004', 'diego.pereira@example.com', 'Rua D',          '40',  NULL,      'Petrópolis',        'Porto Alegre', 'RS', '90450-070', 'IPÊ Saúde',  'M'),
(TRUE, DATE '1999-05-18', 'Eduarda', 'Melo',     '88811144452', '(51) 99555-0005', 'eduarda.melo@example.com',  'Rua E',          '50',  'Apto 101','Tristeza',          'Porto Alegre', 'RS', '91910-080', 'Unimed',     'F'),
(TRUE, DATE '1984-09-03', 'Felipe',  'Gomes',    '99922255563', '(51) 99666-0006', 'felipe.gomes@example.com',  'Rua F',          '60',  NULL,      'Cidade Baixa',      'Porto Alegre', 'RS', '90050-090', 'Cassi',      'M'),
(TRUE, DATE '1975-12-25', 'Gabriela','Souza',    '00033366674', '(51) 99777-0007', 'gabriela.souza@example.com','Rua G',          '70',  'Casa',    'Menino Deus',       'Porto Alegre', 'RS', '90130-100', 'Particular', 'F');

-- ---------------------------------------------------------------------

-- =====================================================================
-- ROLES DO SISTEMA (PERFIS DE ACESSO)
-- =====================================================================
INSERT INTO tb_role (authority) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MEDICO'),
    ('ROLE_SECRETARIO'),
    ('ROLE_ATENDENTE'),
    ('ROLE_ENFERMEIRO'),
    ('ROLE_PACIENTE');

-- =====================================================================
-- USUÁRIOS BASE (SEM PERFIL NA TABELA, APENAS CREDENCIAIS)
-- =====================================================================
INSERT INTO tb_usuarios
(ativo, cpf, email, login, senha, nome, sobrenome, logradouro, numero)
VALUES
-- ADMIN
(TRUE, '32844208606', 'admin@medicflow.com', 'admin',  'admin123', 'System',  'Admin',      'Rua Central', '1'),

-- MÉDICOS (Dr House team)
(TRUE, '39053344705', 'dr.house@example.com',   'drhouse',   'senhaSegura#1', 'Gregory', 'House',   'Rua Médica 1', '10'),
(TRUE, '90011122201', 'dr.cameron@example.com', 'drcameron', 'senhaSegura#2', 'Allison', 'Cameron', 'Rua Médica 2', '20'),
(TRUE, '90011122202', 'dr.foreman@example.com', 'drforeman', 'senhaSegura#3', 'Eric',    'Foreman', 'Rua Médica 3', '30'),
(TRUE, '90011122203', 'dr.chase@example.com',   'drchase',   'senhaSegura#4', 'Robert',  'Chase',   'Rua Médica 4', '40'),

-- SECRETÁRIO
(TRUE, '12345678901', 'sec.marina@example.com', 'marina.sec', 'senha#Sec1', 'Marina', 'Silva', 'Rua Clínica 10', '100'),

-- ATENDENTE
(TRUE, '12345678902', 'at.claudio@example.com', 'claudio.at', 'senha#At1', 'Claudio', 'Pereira', 'Rua Clínica 11', '200'),

-- ENFERMEIRO (subclasse)
(TRUE, '12345678903', 'nf.juliana@example.com', 'juliana.enf', 'senha#Enf1', 'Juliana', 'Rocha', 'Rua Enfermagem 1', '300'),

-- PACIENTE
(TRUE, '98765432100', 'pac.teste@example.com', 'paciente01', 'senha#pac1', 'Carlos', 'Santos', 'Rua Paciente 1', '500');

-- =====================================================================
-- ASSOCIAÇÃO DOS USUÁRIOS COM ROLES
-- =====================================================================

-- ADMIN
INSERT INTO tb_usuario_role (usuario_id, role_id)
VALUES (
    (SELECT id FROM tb_usuarios WHERE login = 'admin'),
    (SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN')
);

-- MÉDICOS
INSERT INTO tb_usuario_role (usuario_id, role_id)
VALUES
((SELECT id FROM tb_usuarios WHERE login = 'drhouse'),   (SELECT id FROM tb_role WHERE authority = 'ROLE_MEDICO')),
((SELECT id FROM tb_usuarios WHERE login = 'drcameron'), (SELECT id FROM tb_role WHERE authority = 'ROLE_MEDICO')),
((SELECT id FROM tb_usuarios WHERE login = 'drforeman'), (SELECT id FROM tb_role WHERE authority = 'ROLE_MEDICO')),
((SELECT id FROM tb_usuarios WHERE login = 'drchase'),   (SELECT id FROM tb_role WHERE authority = 'ROLE_MEDICO'));

-- SECRETÁRIO
INSERT INTO tb_usuario_role (usuario_id, role_id)
VALUES (
    (SELECT id FROM tb_usuarios WHERE login = 'marina.sec'),
    (SELECT id FROM tb_role WHERE authority = 'ROLE_SECRETARIO')
);

-- ATENDENTE
INSERT INTO tb_usuario_role (usuario_id, role_id)
VALUES (
    (SELECT id FROM tb_usuarios WHERE login = 'claudio.at'),
    (SELECT id FROM tb_role WHERE authority = 'ROLE_ATENDENTE')
);

-- ENFERMEIRO
INSERT INTO tb_usuario_role (usuario_id, role_id)
VALUES (
    (SELECT id FROM tb_usuarios WHERE login = 'juliana.enf'),
    (SELECT id FROM tb_role WHERE authority = 'ROLE_ENFERMEIRO')
);

-- PACIENTE
INSERT INTO tb_usuario_role (usuario_id, role_id)
VALUES (
    (SELECT id FROM tb_usuarios WHERE login = 'paciente01'),
    (SELECT id FROM tb_role WHERE authority = 'ROLE_PACIENTE')
);

-- =====================================================================
-- MÉDICOS — TABELA medicos
-- =====================================================================
INSERT INTO tb_medicos
(usuario_id, crm, especialidade, instituicao_formacao, observacoes, sexo, data_formacao)
VALUES
((SELECT id FROM tb_usuarios WHERE login='drhouse'),   'CRM-RS-10001', 'Clínica Geral', 'Princeton-Plainsboro', NULL, 'M', DATE '2000-01-01'),
((SELECT id FROM tb_usuarios WHERE login='drcameron'), 'CRM-RS-10002', 'Imunologia',    'Princeton-Plainsboro', NULL, 'F', DATE '2002-01-01'),
((SELECT id FROM tb_usuarios WHERE login='drforeman'), 'CRM-RS-10003', 'Neurologia',    'Princeton-Plainsboro', NULL, 'M', DATE '2001-01-01'),
((SELECT id FROM tb_usuarios WHERE login='drchase'),   'CRM-RS-10004', 'Cirurgia',      'Princeton-Plainsboro', NULL, 'M', DATE '2003-01-01');

-- =====================================================================
-- ENFERMEIROS — TABELA enfermeiros (JOINED: PK = usuario_id)
-- =====================================================================
INSERT INTO enfermeiros (
    id,                      -- mesma PK do tb_usuarios
    coren,
    uf_coren,
    especialidade_enfermagem,
    setor_clinico,
    turno_trabalho,
    data_admissao,
    observacoes
) VALUES (
    (SELECT id FROM tb_usuarios WHERE login = 'juliana.enf'),
    'COREN-RS-50001',
    'RS',
    'UTI Adulto',
    'UTI',                   -- tem que bater com o Enum SetorClinico.UTI
    'NOITE',                 -- tem que bater com o Enum TurnoTrabalho.NOITE
    DATE '2020-05-10',
    'Profissional com experiência em cuidados intensivos.'
);

-- ---------------------------------------------------------------------

INSERT INTO consultas
(
    data_hora,
    medico_id,
    paciente_id,
    motivo,
    observacoes,
    anamnese,
    diagnostico,
    exame_fisico,
    prescricao,
    status,
    tipo,
    valor_consulta,
    meio_pagamento,
    pago,
    data_pagamento,
    duracao_minutos,
    retorno,
    data_limite_retorno,
    teleconsulta,
    link_acesso,
    plano_saude,
    numero_carteirinha
)
VALUES
-- 1
(TIMESTAMP '2025-01-10 09:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drhouse'),
 1,
 'Cefaleia',
 'Paciente refere dor leve',
 'Anamnese A',
 'Sem diagnóstico fechado',
 'Exame normal',
 NULL,
 'AGENDADA',
 'PRESENCIAL',
 250.00,
 'DEBITO',
 FALSE,
 NULL,
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 2
(TIMESTAMP '2025-01-10 10:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drcameron'),
 2,
 'Alergia',
 'Prurido ocasional',
 'Anamnese B',
 'Rinite alérgica',
 'Exame normal',
 'Loratadina 10mg',
 'CONFIRMADA',
 'PRESENCIAL',
 280.00,
 'CREDITO',
 TRUE,
 TIMESTAMP '2025-01-10 10:05:00',
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 3
(TIMESTAMP '2025-01-11 09:30:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drforeman'),
 3,
 'Tontura',
 NULL,
 'Anamnese C',
 'Vertigem posicional',
 'Romberg +',
 NULL,
 'EM_ATENDIMENTO',
 'RETORNO',
 0.00,
 'DEBITO',
 FALSE,
 NULL,
 40,
 TRUE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 4
(TIMESTAMP '2025-01-11 11:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drchase'),
 4,
 'Dor abdominal',
 'Jejum 8h',
 'Anamnese D',
 'Suspeita colecistite',
 'Dor à palpação',
 NULL,
 'AGENDADA',
 'URGENCIA',
 350.00,
 'DEBITO',
 FALSE,
 NULL,
 60,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 5
(TIMESTAMP '2025-01-12 08:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drhouse'),
 5,
 'Acompanhamento',
 NULL,
 'Anamnese E',
 NULL,
 'Exame normal',
 NULL,
 'CONFIRMADA',
 'TELECONSULTA',
 220.00,
 'PIX',
 TRUE,
 TIMESTAMP '2025-01-12 08:01:00',
 30,
 FALSE,
 NULL,
 TRUE,
 'https://teleconsulta.medicflow/consultas/5',
 NULL,
 NULL),

-- 6
(TIMESTAMP '2025-01-12 09:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drcameron'),
 6,
 'Asma',
 'Dispneia noturna',
 'Anamnese F',
 'Asma persistente',
 'Sibilos',
 'Corticoide inalatório',
 'CONCLUIDA',
 'PRESENCIAL',
 260.00,
 'DEBITO',
 TRUE,
 TIMESTAMP '2025-01-12 09:10:00',
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 7
(TIMESTAMP '2025-01-13 13:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drforeman'),
 7,
 'Enxaqueca',
 NULL,
 'Anamnese G',
 'Migrânea',
 'Exame normal',
 NULL,
 'CANCELADA',
 'RETORNO',
 0.00,
 'DEBITO',
 FALSE,
 NULL,
 30,
 TRUE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 8
(TIMESTAMP '2025-01-13 14:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drchase'),
 8,
 'Lombalgia',
 'Dor há 3 semanas',
 'Anamnese H',
 'Lombalgia mecânica',
 'Dor L5',
 'Analgésico',
 'CONCLUIDA',
 'PRESENCIAL',
 270.00,
 'PIX',
 TRUE,
 TIMESTAMP '2025-01-13 14:20:00',
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 9
(TIMESTAMP '2025-01-14 15:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drhouse'),
 9,
 'Gripe',
 NULL,
 'Anamnese I',
 'Síndrome gripal',
 'Febre 38.2',
 NULL,
 'CONFIRMADA',
 'TELECONSULTA',
 230.00,
 'PIX',
 TRUE,
 TIMESTAMP '2025-01-14 15:02:00',
 20,
 FALSE,
 NULL,
 TRUE,
 'https://teleconsulta.medicflow/consultas/9',
 NULL,
 NULL),

-- 10
(TIMESTAMP '2025-01-14 16:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drcameron'),
 10,
 'Dismenorreia',
 'Cólica intensa',
 'Anamnese J',
 NULL,
 'Exame normal',
 'Antiespasmódico',
 'AGENDADA',
 'PRESENCIAL',
 250.00,
 'CREDITO',
 FALSE,
 NULL,
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 11
(TIMESTAMP '2025-02-10 09:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drhouse'),
 1,
 'Checkup',
 NULL,
 'Anamnese K',
 'Saudável',
 'Exame normal',
 NULL,
 'CONCLUIDA',
 'PRESENCIAL',
 300.00,
 'DEBITO',
 TRUE,
 TIMESTAMP '2025-02-10 09:30:00',
 40,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 12
(TIMESTAMP '2025-02-10 10:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drcameron'),
 2,
 'Alergia pó',
 NULL,
 'Anamnese L',
 'Rinite',
 'Exame normal',
 'Anti-histamínico',
 'CONCLUIDA',
 'RETORNO',
 200.00,
 'CREDITO',
 TRUE,
 TIMESTAMP '2025-02-10 10:15:00',
 30,
 TRUE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 13
(TIMESTAMP '2025-02-11 09:30:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drforeman'),
 3,
 'Tremor',
 NULL,
 'Anamnese M',
 'Tremor essencial',
 'Leve tremor',
 NULL,
 'AGENDADA',
 'PRESENCIAL',
 250.00,
 'DEBITO',
 FALSE,
 NULL,
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 14
(TIMESTAMP '2025-02-11 11:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drchase'),
 4,
 'Náusea',
 'Desde ontem',
 'Anamnese N',
 NULL,
 'Exame normal',
 'Ondansetrona',
 'CONFIRMADA',
 'TELECONSULTA',
 240.00,
 'PIX',
 TRUE,
 TIMESTAMP '2025-02-11 11:05:00',
 20,
 FALSE,
 NULL,
 TRUE,
 'https://teleconsulta.medicflow/consultas/14',
 NULL,
 NULL),

-- 15
(TIMESTAMP '2025-02-12 08:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drhouse'),
 5,
 'Dor torácica',
 'Esforço físico',
 'Anamnese O',
 'Musculoesquelética',
 'Dor à palpação',
 NULL,
 'CONCLUIDA',
 'PRESENCIAL',
 320.00,
 'CREDITO',
 TRUE,
 TIMESTAMP '2025-02-12 08:20:00',
 40,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 16
(TIMESTAMP '2025-02-12 09:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drcameron'),
 6,
 'Urticária',
 NULL,
 'Anamnese P',
 NULL,
 'Pápulas',
 'Anti-histamínico',
 'CONCLUIDA',
 'PRESENCIAL',
 260.00,
 'DEBITO',
 TRUE,
 TIMESTAMP '2025-02-12 09:05:00',
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 17
(TIMESTAMP '2025-02-13 13:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drforeman'),
 7,
 'Parestesia',
 NULL,
 'Anamnese Q',
 'Ansiedade',
 'Exame normal',
 NULL,
 'EM_ATENDIMENTO',
 'RETORNO',
 180.00,
 'PIX',
 FALSE,
 NULL,
 30,
 TRUE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 18
(TIMESTAMP '2025-02-13 14:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drchase'),
 8,
 'Sinusite',
 'Secreção espessa',
 'Anamnese R',
 'Sinusite bacteriana',
 'Dor seios face',
 'Amoxicilina',
 'CONCLUIDA',
 'PRESENCIAL',
 270.00,
 'CREDITO',
 TRUE,
 TIMESTAMP '2025-02-13 14:25:00',
 30,
 FALSE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL),

-- 19
(TIMESTAMP '2025-02-14 15:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drhouse'),
 9,
 'Resfriado',
 NULL,
 'Anamnese S',
 'Viral',
 'Exame normal',
 NULL,
 'CONFIRMADA',
 'TELECONSULTA',
 230.00,
 'PIX',
 TRUE,
 TIMESTAMP '2025-02-14 15:03:00',
 20,
 FALSE,
 NULL,
 TRUE,
 'https://teleconsulta.medicflow/consultas/19',
 NULL,
 NULL),

-- 20
(TIMESTAMP '2025-02-14 16:00:00',
 (SELECT usuario_id FROM tb_medicos m JOIN tb_usuarios u ON u.id = m.usuario_id WHERE u.login = 'drcameron'),
 10,
 'Tensão pré-menstrual',
 'Irritabilidade',
 'Anamnese T',
 NULL,
 'Exame normal',
 'Sintomáticos',
 'AGENDADA',
 'RETORNO',
 200.00,
 'DEBITO',
 FALSE,
 NULL,
 30,
 TRUE,
 NULL,
 FALSE,
 NULL,
 NULL,
 NULL);


-- ---------------------------------------------------------------------

INSERT INTO medicamento_base (id, nome_comercial)
SELECT NEXT VALUE FOR medicamento_base_seq, 'Paracetamol 750mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Ibuprofeno 200mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Dipirona 500mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Amoxicilina 500mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Azitromicina 500mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Loratadina 10mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Prednisona 20mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Omeprazol 20mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Ranitidina 150mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Ondansetrona 8mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Losartana 50mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Hidroclorotiazida 25mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Metformina 850mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Glibenclamida 5mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Atorvastatina 20mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Sinvastatina 20mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'AAS 100mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Clopidogrel 75mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Salbutamol Spray' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Budesonida Inalatória' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Cetirizina 10mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Naproxeno 250mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Cefalexina 500mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Ciprofloxacino 500mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Fluconazol 150mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Diclofenaco 50mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Amoxicilina+Clav 875/125' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Albendazol 400mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Dexametasona 4mg' UNION ALL
SELECT NEXT VALUE FOR medicamento_base_seq, 'Furosemida 40mg';

-- ---------------------------------------------------------------------

INSERT INTO exame_base (nome, codigo_tuss, tipo, prazo_estimado) VALUES
('Hemograma completo','10101012','LABORATORIAL',2),
('Glicemia de jejum','10102010','LABORATORIAL',1),
('Colesterol total','10103011','LABORATORIAL',3),
('Triglicerídeos','10104010','LABORATORIAL',3),
('TSH','10105010','LABORATORIAL',5),
('T4 Livre','10106010','LABORATORIAL',5),
('PCR (Proteína C Reativa)','10107010','LABORATORIAL',2),
('Ureia','10108010','LABORATORIAL',2),
('Creatinina','10109010','LABORATORIAL',2),
('EAS (Urina tipo 1)','10110010','LABORATORIAL',1),
('Raio-X Tórax','20101010','IMAGEM',2),
('USG Abdome Total','20102010','IMAGEM',5),
('ECG (Eletrocardiograma)','20103010','IMAGEM',1),
('Tomografia de Crânio','20104010','OUTRO',7),
('Ressonância de Joelho','20105010','OUTRO',10);

-- ---------------------------------------------------------------------

INSERT INTO medicamento_prescrito
(consulta_id, medicamento_base_id, paciente_id, nome, dosagem, frequencia, via)
VALUES
(1,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Paracetamol 750mg'), 1,  'Paracetamol 750mg', '750 mg',    '8/8h por 3 dias', 'VO'),
(2,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Loratadina 10mg'),   2,  'Loratadina 10mg',   '10 mg',     '1x/dia',          'VO'),
(4,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Amoxicilina+Clav 875/125'), 4,  'Amoxi+Clav 875/125','875/125 mg','12/12h por 7 dias','VO'),
(6,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Salbutamol Spray'),  6,  'Salbutamol Spray',  '2 jatos',   'sob demanda',     'Inalatória'),
(6,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Budesonida Inalatória'), 6,  'Budesonida',        '200 mcg',   '2x/dia',          'Inalatória'),
(8,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Naproxeno 250mg'),   8,  'Naproxeno 250mg',   '250 mg',    '12/12h por 5 dias','VO'),
(9,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Paracetamol 750mg'), 9,  'Paracetamol 750mg', '750 mg',    '8/8h por 2 dias', 'VO'),
(10, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Ondansetrona 8mg'), 10,  'Ondansetrona 8mg',  '8 mg',      '8/8h por 2 dias', 'VO'),
(11, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='AAS 100mg'),         1,   'AAS 100mg',         '100 mg',    '1x/dia',          'VO'),
(12, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Cetirizina 10mg'),    2,   'Cetirizina 10mg',   '10 mg',     '1x/dia',          'VO'),
(13, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Diclofenaco 50mg'),   3,   'Diclofenaco 50mg',  '50 mg',     '8/8h por 3 dias', 'VO'),
(14, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Omeprazol 20mg'),     4,   'Omeprazol 20mg',    '20 mg',     '1x/dia',          'VO'),
(15, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Losartana 50mg'),     5,   'Losartana 50mg',    '50 mg',     '1x/dia',          'VO'),
(16, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Loratadina 10mg'),    6,   'Loratadina 10mg',   '10 mg',     '1x/dia',          'VO'),
(17, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Dipirona 500mg'),     7,   'Dipirona 500mg',    '500 mg',    '6/6h por 2 dias', 'VO'),
(18, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Cefalexina 500mg'),   8,   'Cefalexina 500mg',  '500 mg',    '6/6h por 7 dias', 'VO'),
(19, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Paracetamol 750mg'),  9,   'Paracetamol 750mg', '750 mg',    '8/8h por 2 dias', 'VO'),
(20, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Loratadina 10mg'),    10,  'Loratadina 10mg',   '10 mg',     '1x/dia',          'VO'),
(3,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Dexametasona 4mg'),   3,   'Dexametasona 4mg',  '4 mg',      '1x/dia por 3 dias','VO'),
(5,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Omeprazol 20mg'),     5,   'Omeprazol 20mg',    '20 mg',     '1x/dia',          'VO'),
(7,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Ibuprofeno 200mg'),   7,   'Ibuprofeno 200mg',  '200 mg',    '8/8h por 3 dias', 'VO'),
(12, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Salbutamol Spray'),    6,   'Salbutamol Spray',  '2 jatos',   'sob demanda',     'Inalatória'),
(18, (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Ciprofloxacino 500mg'),8,   'Ciprofloxacino',    '500 mg',    '12/12h por 7 dias','VO'),
(4,  (SELECT MIN(id) FROM medicamento_base WHERE nome_comercial='Ondansetrona 8mg'),    4,   'Ondansetrona 8mg',  '8 mg',      '8/8h se náusea',  'VO');

-- ---------------------------------------------------------------------

INSERT INTO exame_solicitado (consulta_id, exame_base_id, status, justificativa, observacoes)
VALUES
(1, 1,  'SOLICITADO', 'Rastreamento inicial: hemograma.', 'Paciente em avaliação'),
(1, 8,  'SOLICITADO', 'Função renal por dor lombar.',     'Solicitado junto ao hemograma'),
(2, 10, 'SOLICITADO', 'Sintomas urinários recentes.',      'EAS para triagem');

INSERT INTO exame_solicitado (consulta_id, exame_base_id, status, data_coleta, justificativa, observacoes)
VALUES
(3, 12, 'AGENDADO', DATEADD('DAY',  1, CURRENT_TIMESTAMP), 'Dor abdominal, avaliar hepatobiliar.', 'USG marcado para amanhã 09h'),
(3, 11, 'AGENDADO', DATEADD('DAY',  2, CURRENT_TIMESTAMP), 'Dispneia leve, RX de tórax.',          'Radiografia no mesmo serviço');

INSERT INTO exame_solicitado (consulta_id, exame_base_id, status, data_coleta, data_resultado, justificativa, observacoes)
VALUES
(4, 2,  'REALIZADO', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'Glicemia de jejum por suspeita de DM.', 'Resultado disponível no prontuário'),
(4, 3,  'REALIZADO', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'Perfil lipídico: colesterol total.',    'Valores limítrofes'),
(5, 7,  'REALIZADO', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('HOUR', -6, CURRENT_TIMESTAMP), 'Inflamação: PCR.',                      'PCR discretamente elevado');

INSERT INTO exame_solicitado (consulta_id, exame_base_id, status, justificativa, observacoes)
VALUES
(6, 14, 'CANCELADO', 'Paciente não compareceu para tomografia.', 'Remarcar conforme disponibilidade'),
(6, 5,  'CANCELADO', 'Solicitação duplicada detectada.',          'Cancelado pela recepção');

INSERT INTO exame_solicitado (consulta_id, exame_base_id, status, data_coleta, justificativa, observacoes)
VALUES
(7, 13, 'AGENDADO', DATEADD('DAY', 3, CURRENT_TIMESTAMP), 'Arritmia: solicitar ECG.', 'Agendado via convênio');

INSERT INTO exame_solicitado (consulta_id, exame_base_id, status, data_coleta, data_resultado, justificativa, observacoes)
VALUES
(8, 15, 'REALIZADO', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'Dor no joelho, suspeita de lesão.', 'Ressonância concluída, laudo anexado');
