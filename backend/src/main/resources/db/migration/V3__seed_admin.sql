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
) VALUES (
    TRUE,
    '39053344705',
    'admin@medicflow.com',
    'admin',
    '$2a$12$Nnggmf74vk2TdWawL.lHMuypRJH8tDLTzEvR9VSQHyfvQh7QLlhxu',
    'Administrador',
    'Sistema',
    'Rua Principal',
    '100'
);

INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT u.id, r.id
FROM tb_usuarios u
JOIN tb_role r ON r.authority = 'ROLE_ADMIN'
WHERE u.login = 'admin';
