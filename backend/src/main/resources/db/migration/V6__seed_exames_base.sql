
INSERT INTO exame_base (
    nome,
    codigo_tuss,
    tipo,
    prazo_estimado
)
SELECT
    t.nome,
    t.codigo_tuss,
    t.tipo,
    t.prazo_estimado
FROM (
    VALUES
        ('Hemograma completo', '10101012', 'LABORATORIAL', 2),
        ('Glicemia de jejum', '10102010', 'LABORATORIAL', 1),
        ('Colesterol total', '10103015', 'LABORATORIAL', 2),
        ('Triglicerídeos', '10103023', 'LABORATORIAL', 2),
        ('Creatinina', '10104011', 'LABORATORIAL', 1),
        ('Ureia', '10104020', 'LABORATORIAL', 1),
        ('TSH', '10105018', 'LABORATORIAL', 3),
        ('T4 livre', '10105026', 'LABORATORIAL', 3),
        ('Urina tipo 1', '10106014', 'LABORATORIAL', 2),
        ('Urocultura', '10106022', 'LABORATORIAL', 4),

        ('Raio-X de tórax', '20101010', 'IMAGEM', 1),
        ('Ultrassonografia abdominal total', '20102018', 'IMAGEM', 2),
        ('Tomografia computadorizada de crânio', '20103016', 'IMAGEM', 3),
        ('Ressonância magnética de coluna lombar', '20104013', 'IMAGEM', 5),
        ('Mamografia bilateral', '20105011', 'IMAGEM', 4),

        ('Eletrocardiograma', '20201011', 'OUTRO', 1),
        ('Ecocardiograma transtorácico', '20202019', 'OUTRO', 3),
        ('Teste ergométrico', '20203016', 'OUTRO', 3),
        ('Endoscopia digestiva alta', '20301017', 'OUTRO', 4),
        ('Colonoscopia', '20302015', 'OUTRO', 5)
) AS t(nome, codigo_tuss, tipo, prazo_estimado)
WHERE NOT EXISTS (
    SELECT 1
    FROM exame_base e
    WHERE LOWER(e.nome) = LOWER(t.nome)
);

