BEGIN;

INSERT INTO exame_base (nome, codigo_tuss, tipo, prazo_estimado)
SELECT
    v.nome,
    v.codigo_tuss,
    v.tipo,
    v.prazo_estimado
FROM (
    VALUES
        ('Hemograma completo', '10101012', 'LABORATORIAL', 2),
        ('Glicemia de jejum', '10102010', 'LABORATORIAL', 1),
        ('Hemoglobina glicada', '10102011', 'LABORATORIAL', 3),
        ('Insulina de jejum', '10102012', 'LABORATORIAL', 4),
        ('Colesterol total', '10103011', 'LABORATORIAL', 3),
        ('HDL colesterol', '10103012', 'LABORATORIAL', 3),
        ('LDL colesterol', '10103013', 'LABORATORIAL', 3),
        ('Triglicerídeos', '10104010', 'LABORATORIAL', 3),
        ('Ureia', '10108010', 'LABORATORIAL', 2),
        ('Creatinina', '10109010', 'LABORATORIAL', 2),
        ('Sódio', '10109011', 'LABORATORIAL', 2),
        ('Potássio', '10109012', 'LABORATORIAL', 2),
        ('Cálcio sérico', '10109013', 'LABORATORIAL', 3),
        ('Magnésio', '10109014', 'LABORATORIAL', 3),
        ('TGO (AST)', '10111010', 'LABORATORIAL', 3),
        ('TGP (ALT)', '10111011', 'LABORATORIAL', 3),
        ('Gama GT', '10111012', 'LABORATORIAL', 3),
        ('Fosfatase alcalina', '10111013', 'LABORATORIAL', 3),
        ('Bilirrubinas totais e frações', '10111014', 'LABORATORIAL', 3),
        ('Albumina', '10111015', 'LABORATORIAL', 3),
        ('TSH', '10105010', 'LABORATORIAL', 5),
        ('T4 Livre', '10106010', 'LABORATORIAL', 5),
        ('T3 Livre', '10106011', 'LABORATORIAL', 5),
        ('PCR (Proteína C Reativa)', '10107010', 'LABORATORIAL', 2),
        ('VHS', '10107011', 'LABORATORIAL', 2),
        ('Ferritina', '10112010', 'LABORATORIAL', 4),
        ('Ferro sérico', '10112011', 'LABORATORIAL', 4),
        ('Vitamina B12', '10112012', 'LABORATORIAL', 5),
        ('Vitamina D', '10112013', 'LABORATORIAL', 5),
        ('Ácido fólico', '10112014', 'LABORATORIAL', 5),
        ('EAS (Urina tipo 1)', '10110010', 'LABORATORIAL', 1),
        ('Urocultura', '10110011', 'LABORATORIAL', 4),
        ('Parcial de fezes', '10110012', 'LABORATORIAL', 2),
        ('Pesquisa de sangue oculto nas fezes', '10110013', 'LABORATORIAL', 3),
        ('Coagulograma', '10113010', 'LABORATORIAL', 2),
        ('TAP/INR', '10113011', 'LABORATORIAL', 1),
        ('TTPA', '10113012', 'LABORATORIAL', 1),
        ('Beta HCG quantitativo', '10114010', 'LABORATORIAL', 1),
        ('PSA total', '10115010', 'LABORATORIAL', 4),
        ('PSA livre', '10115011', 'LABORATORIAL', 4),

        ('Raio-X de tórax', '20101010', 'IMAGEM', 2),
        ('Raio-X de seios da face', '20101011', 'IMAGEM', 2),
        ('Raio-X de coluna lombar', '20101012', 'IMAGEM', 2),
        ('Raio-X de joelho', '20101013', 'IMAGEM', 2),
        ('Ultrassonografia de abdome total', '20102010', 'IMAGEM', 5),
        ('Ultrassonografia de abdome superior', '20102011', 'IMAGEM', 4),
        ('Ultrassonografia pélvica', '20102012', 'IMAGEM', 4),
        ('Ultrassonografia de tireoide', '20102013', 'IMAGEM', 4),
        ('Ultrassonografia de mamas', '20102014', 'IMAGEM', 5),
        ('Ecocardiograma transtorácico', '20103011', 'IMAGEM', 5),
        ('Eletrocardiograma', '20103010', 'IMAGEM', 1),
        ('Mapa 24 horas', '20103012', 'IMAGEM', 5),
        ('Holter 24 horas', '20103013', 'IMAGEM', 5),
        ('Teste ergométrico', '20103014', 'IMAGEM', 4),
        ('Tomografia de crânio', '20104010', 'OUTRO', 7),
        ('Tomografia de tórax', '20104011', 'OUTRO', 7),
        ('Tomografia de abdome total', '20104012', 'OUTRO', 8),
        ('Tomografia de seios da face', '20104013', 'OUTRO', 7),
        ('Ressonância magnética de joelho', '20105010', 'OUTRO', 10),
        ('Ressonância magnética de coluna lombar', '20105011', 'OUTRO', 10),
        ('Ressonância magnética de crânio', '20105012', 'OUTRO', 10),
        ('Mamografia bilateral', '20106010', 'IMAGEM', 7),
        ('Densitometria óssea', '20106011', 'IMAGEM', 7),
        ('Endoscopia digestiva alta', '30101010', 'OUTRO', 8),
        ('Colonoscopia', '30101011', 'OUTRO', 10),
        ('Espirometria', '30102010', 'OUTRO', 4)
) AS v(nome, codigo_tuss, tipo, prazo_estimado)
WHERE NOT EXISTS (
    SELECT 1
    FROM exame_base eb
    WHERE eb.codigo_tuss = v.codigo_tuss
);

COMMIT;