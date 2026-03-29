

INSERT INTO medicamento_base (
    nome_comercial,
    principio_ativo,
    dcb,
    forma_farmaceutica,
    dosagem_padrao,
    concentracao,
    via_administracao,
    codigo_atc,
    registro_anvisa,
    fabricante,
    controlado,
    ativo
)
SELECT
    t.nome_comercial,
    t.principio_ativo,
    t.dcb,
    t.forma_farmaceutica,
    t.dosagem_padrao,
    t.concentracao,
    t.via_administracao,
    t.codigo_atc,
    t.registro_anvisa,
    t.fabricante,
    t.controlado,
    t.ativo
FROM (
    VALUES
        ('Paracetamol', 'Paracetamol', 'paracetamol', 'Comprimido', '500 mg', '500 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Ibuprofeno', 'Ibuprofeno', 'ibuprofeno', 'Comprimido', '400 mg', '400 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Dipirona', 'Dipirona Sódica', 'dipirona sodica', 'Comprimido', '500 mg', '500 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Amoxicilina', 'Amoxicilina', 'amoxicilina', 'Cápsula', '500 mg', '500 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Omeprazol', 'Omeprazol', 'omeprazol', 'Cápsula', '20 mg', '20 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Loratadina', 'Loratadina', 'loratadina', 'Comprimido', '10 mg', '10 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Prednisona', 'Prednisona', 'prednisona', 'Comprimido', '20 mg', '20 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Metformina', 'Cloridrato de Metformina', 'metformina', 'Comprimido', '850 mg', '850 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Losartana', 'Losartana Potássica', 'losartana potassica', 'Comprimido', '50 mg', '50 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Sinvastatina', 'Sinvastatina', 'sinvastatina', 'Comprimido', '20 mg', '20 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Azitromicina', 'Azitromicina', 'azitromicina', 'Comprimido', '500 mg', '500 mg', 'Oral', NULL, NULL, NULL, false, true),
        ('Tramadol', 'Cloridrato de Tramadol', 'tramadol', 'Comprimido', '50 mg', '50 mg', 'Oral', NULL, NULL, NULL, true, true),
        ('Morfina', 'Sulfato de Morfina', 'morfina', 'Comprimido', '10 mg', '10 mg', 'Oral', NULL, NULL, NULL, true, true),
        ('Diazepam', 'Diazepam', 'diazepam', 'Comprimido', '10 mg', '10 mg', 'Oral', NULL, NULL, NULL, true, true)
) AS t(
    nome_comercial,
    principio_ativo,
    dcb,
    forma_farmaceutica,
    dosagem_padrao,
    concentracao,
    via_administracao,
    codigo_atc,
    registro_anvisa,
    fabricante,
    controlado,
    ativo
)
WHERE NOT EXISTS (
    SELECT 1
    FROM medicamento_base m
    WHERE LOWER(m.nome_comercial) = LOWER(t.nome_comercial)
);

