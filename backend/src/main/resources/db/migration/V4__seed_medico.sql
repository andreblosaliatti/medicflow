

-- 1) Cria o usuário médico apenas se ainda não existir por login OU cpf
INSERT INTO tb_usuarios (
    ativo,
    cpf,
    email,
    login,
    senha,
    nome,
    sobrenome,
    logradouro,
    numero
)
SELECT
    TRUE,
    '11144477735',
    'medico@medicflow.com',
    'medico',
    '$2a$10$7QJp7P3ZC7H9v9Zzj6Q8n.2qjM9y7x7G6YQ1v1yY7QzZ9QYy6zJ5e',
    'Gregory',
    'House',
    'Rua Médica',
    '123'
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_usuarios u
    WHERE u.login = 'medico'
       OR u.cpf = '11144477735'
);

-- 2) Vincula o usuário à role ROLE_MEDICO
INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuarios u
JOIN tb_role r ON r.authority = 'ROLE_MEDICO'
WHERE u.login = 'medico'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_usuario_role ur
      WHERE ur.usuario_id = u.id
        AND ur.role_id = r.id
  );

-- 3) Cria o registro de médico, se ainda não existir
INSERT INTO tb_medicos (usuario_id, crm, especialidade)
SELECT
    u.id,
    '123456-RS',
    'Clínico Geral'
FROM tb_usuarios u
WHERE u.login = 'medico'
  AND NOT EXISTS (
      SELECT 1
      FROM tb_medicos m
      WHERE m.usuario_id = u.id
  );