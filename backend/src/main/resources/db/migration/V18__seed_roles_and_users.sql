BEGIN;

-- Seed de perfis base
INSERT INTO tb_role (authority)
SELECT v.authority
FROM (
    VALUES
        ('ROLE_ADMIN'),
        ('ROLE_MEDICO'),
        ('ROLE_SECRETARIA'),
        ('ROLE_ATENDENTE'),
        ('ROLE_ENFERMEIRO'),
        ('ROLE_PACIENTE')
) AS v(authority)
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_role r
    WHERE r.authority = v.authority
);

-- Seed canônico de usuários base
-- 1) Atualiza por login quando já existe
UPDATE tb_usuarios u
SET
    ativo = v.ativo,
    cpf = v.cpf,
    email = v.email,
    senha = v.senha,
    nome = v.nome,
    sobrenome = v.sobrenome,
    logradouro = v.logradouro,
    numero = v.numero
FROM (
    VALUES
        (TRUE, '32844208606', 'admin@medicflow.com',    'admin',       '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'System',  'Admin',   'Rua Central',      '1'),
        (TRUE, '39053344705', 'dr.house@example.com',   'drhouse',     '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Gregory', 'House',   'Rua Médica 1',     '10'),
        (TRUE, '90011122201', 'dr.cameron@example.com', 'drcameron',   '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Allison', 'Cameron', 'Rua Médica 2',     '20'),
        (TRUE, '12345678903', 'nf.juliana@example.com', 'juliana.enf', '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Juliana', 'Rocha',   'Rua Enfermagem 1', '300'),
        (TRUE, '12345678901', 'sec.marina@example.com', 'marinasec',   '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Marina',  'Silva',   'Rua Clínica 10',   '100')
) AS v(ativo, cpf, email, login, senha, nome, sobrenome, logradouro, numero)
WHERE u.login = v.login;

-- 2) Insere quando nenhum identificador único conflita (login, email, cpf)
INSERT INTO tb_usuarios
(ativo, cpf, email, login, senha, nome, sobrenome, logradouro, numero)
SELECT
    v.ativo,
    v.cpf,
    v.email,
    v.login,
    v.senha,
    v.nome,
    v.sobrenome,
    v.logradouro,
    v.numero
FROM (
    VALUES
        (TRUE, '32844208606', 'admin@medicflow.com',    'admin',       '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'System',  'Admin',   'Rua Central',      '1'),
        (TRUE, '39053344705', 'dr.house@example.com',   'drhouse',     '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Gregory', 'House',   'Rua Médica 1',     '10'),
        (TRUE, '90011122201', 'dr.cameron@example.com', 'drcameron',   '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Allison', 'Cameron', 'Rua Médica 2',     '20'),
        (TRUE, '12345678903', 'nf.juliana@example.com', 'juliana.enf', '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Juliana', 'Rocha',   'Rua Enfermagem 1', '300'),
        (TRUE, '12345678901', 'sec.marina@example.com', 'marinasec',   '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu', 'Marina',  'Silva',   'Rua Clínica 10',   '100')
) AS v(ativo, cpf, email, login, senha, nome, sobrenome, logradouro, numero)
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_usuarios u
    WHERE u.login = v.login
       OR u.email = v.email
       OR u.cpf = v.cpf
);

-- Vínculo usuário x perfil
INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM (
    VALUES
        ('admin', 'ROLE_ADMIN'),
        ('drhouse', 'ROLE_MEDICO'),
        ('drcameron', 'ROLE_MEDICO'),
        ('juliana.enf', 'ROLE_ENFERMEIRO'),
        ('marinasec', 'ROLE_SECRETARIA')
) AS v(login, authority)
JOIN tb_usuarios u ON u.login = v.login
JOIN tb_role r ON r.authority = v.authority
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_usuario_role ur
    WHERE ur.usuario_id = u.id
      AND ur.role_id = r.id
);

-- Profissionais médicos
INSERT INTO tb_medicos
(usuario_id, crm, especialidade, instituicao_formacao, observacoes, sexo, data_formacao)
SELECT
    u.id,
    v.crm,
    v.especialidade,
    v.instituicao_formacao,
    v.observacoes,
    v.sexo,
    v.data_formacao
FROM (
    VALUES
        ('drhouse',   'CRM-RS-10001', 'Clínica Geral', 'Princeton-Plainsboro', NULL, 'M', DATE '2000-01-01'),
        ('drcameron', 'CRM-RS-10002', 'Imunologia',    'Princeton-Plainsboro', NULL, 'F', DATE '2002-01-01')
) AS v(login, crm, especialidade, instituicao_formacao, observacoes, sexo, data_formacao)
JOIN tb_usuarios u ON u.login = v.login
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_medicos m
    WHERE m.usuario_id = u.id
       OR m.crm = v.crm
);

-- Profissional enfermeiro
INSERT INTO enfermeiros
(id, coren, uf_coren, especialidade_enfermagem, setor_clinico, turno_trabalho, data_admissao, observacoes)
SELECT
    u.id,
    'COREN-RS-50001',
    'RS',
    'UTI Adulto',
    'UTI',
    'NOITE',
    DATE '2020-05-10',
    'Seed inicial'
FROM tb_usuarios u
WHERE u.login = 'juliana.enf'
  AND NOT EXISTS (
      SELECT 1
      FROM enfermeiros e
      WHERE e.id = u.id
         OR e.coren = 'COREN-RS-50001'
  );

COMMIT;
