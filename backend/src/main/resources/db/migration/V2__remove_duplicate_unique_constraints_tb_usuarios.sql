ALTER TABLE tb_usuarios
    DROP CONSTRAINT IF EXISTS tb_usuarios_cpf_key;

ALTER TABLE tb_usuarios
    DROP CONSTRAINT IF EXISTS tb_usuarios_login_key;