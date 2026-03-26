BEGIN;

-- =========================================================
-- 1) ROLES
-- =========================================================

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
    SELECT 1 FROM tb_role r WHERE r.authority = v.authority
);

-- =========================================================
-- 2) USUÁRIOS
-- =========================================================

INSERT INTO tb_usuarios
(ativo, cpf, email, login, senha, nome, sobrenome, logradouro, numero)
SELECT *
FROM (
    VALUES
    (TRUE, '32844208606', 'admin@medicflow.com',    'admin',      '$2a$12$...', 'System',  'Admin',   'Rua Central',      '1'),
    (TRUE, '39053344705', 'dr.house@example.com',   'drhouse',    '$2a$12$...', 'Gregory', 'House',   'Rua Médica 1',     '10'),
    (TRUE, '90011122201', 'dr.cameron@example.com', 'drcameron',  '$2a$12$...', 'Allison', 'Cameron', 'Rua Médica 2',     '20'),
    (TRUE, '12345678903', 'nf.juliana@example.com', 'juliana.enf','$2a$12$...', 'Juliana', 'Rocha',   'Rua Enfermagem 1', '300'),
    (TRUE, '12345678901', 'sec.marina@example.com', 'marinasec',  '$2a$12$...', 'Marina',  'Silva',   'Rua Clínica 10',   '100')
) AS v(ativo, cpf, email, login, senha, nome, sobrenome, logradouro, numero)
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_usuarios u
    WHERE u.login = v.login
       OR u.email = v.email
       OR u.cpf = v.cpf
);

-- =========================================================
-- 3) USUÁRIO x ROLE
-- =========================================================

INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuarios u
JOIN tb_role r ON r.authority = 'ROLE_ADMIN'
WHERE u.login = 'admin'
AND NOT EXISTS (
    SELECT 1 FROM tb_usuario_role ur WHERE ur.usuario_id = u.id AND ur.role_id = r.id
);

INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuarios u
JOIN tb_role r ON r.authority = 'ROLE_MEDICO'
WHERE u.login IN ('drhouse', 'drcameron')
AND NOT EXISTS (
    SELECT 1 FROM tb_usuario_role ur WHERE ur.usuario_id = u.id AND ur.role_id = r.id
);

INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuarios u
JOIN tb_role r ON r.authority = 'ROLE_ENFERMEIRO'
WHERE u.login = 'juliana.enf'
AND NOT EXISTS (
    SELECT 1 FROM tb_usuario_role ur WHERE ur.usuario_id = u.id AND ur.role_id = r.id
);

INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuarios u
JOIN tb_role r ON r.authority = 'ROLE_SECRETARIA'
WHERE u.login = 'marinasec'
AND NOT EXISTS (
    SELECT 1 FROM tb_usuario_role ur WHERE ur.usuario_id = u.id AND ur.role_id = r.id
);

-- =========================================================
-- 4) MÉDICOS
-- =========================================================

INSERT INTO tb_medicos
(usuario_id, crm, especialidade, instituicao_formacao, observacoes, sexo, data_formacao)
SELECT u.id, v.crm, v.especialidade, v.instituicao, NULL, v.sexo, v.data_formacao
FROM (
    VALUES
        ('drhouse',   'CRM-RS-10001', 'Clínica Geral', 'Princeton-Plainsboro', 'M', DATE '2000-01-01'),
        ('drcameron', 'CRM-RS-10002', 'Imunologia',    'Princeton-Plainsboro', 'F', DATE '2002-01-01')
) AS v(login, crm, especialidade, instituicao, sexo, data_formacao)
JOIN tb_usuarios u ON u.login = v.login
WHERE NOT EXISTS (
    SELECT 1 FROM tb_medicos m WHERE m.usuario_id = u.id
);

-- =========================================================
-- 5) ENFERMEIRO
-- =========================================================

INSERT INTO enfermeiros
(id, coren, uf_coren, especialidade_enfermagem, setor_clinico, turno_trabalho, data_admissao, observacoes)
SELECT u.id, 'COREN-RS-50001', 'RS', 'UTI Adulto', 'UTI', 'NOITE', DATE '2020-05-10', 'Seed inicial'
FROM tb_usuarios u
WHERE u.login = 'juliana.enf'
AND NOT EXISTS (
    SELECT 1 FROM enfermeiros e WHERE e.id = u.id
);

COMMIT;