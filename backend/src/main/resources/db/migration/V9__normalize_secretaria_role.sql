INSERT INTO tb_role (authority)
SELECT 'ROLE_SECRETARIA'
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_role
    WHERE authority = 'ROLE_SECRETARIA'
);

INSERT INTO tb_usuario_role (usuario_id, role_id)
SELECT DISTINCT ur.usuario_id, canonical.id
FROM tb_usuario_role ur
JOIN tb_role legacy ON legacy.id = ur.role_id
CROSS JOIN (
    SELECT id
    FROM tb_role
    WHERE authority = 'ROLE_SECRETARIA'
) canonical
WHERE legacy.authority IN ('ROLE_SECRETARIO', 'SECRETARIO', 'SECRETARIA')
ON CONFLICT DO NOTHING;

DELETE FROM tb_usuario_role
WHERE role_id IN (
    SELECT id
    FROM tb_role
    WHERE authority IN ('ROLE_SECRETARIO', 'SECRETARIO', 'SECRETARIA')
);

DELETE FROM tb_role
WHERE authority IN ('ROLE_SECRETARIO', 'SECRETARIO', 'SECRETARIA');
