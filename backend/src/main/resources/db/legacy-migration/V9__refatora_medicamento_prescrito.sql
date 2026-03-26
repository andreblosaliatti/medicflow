ALTER TABLE medicamento_prescrito
    RENAME COLUMN nome TO nome_prescrito;

ALTER TABLE medicamento_prescrito
    RENAME COLUMN dosagem TO dosagem_prescrita;

ALTER TABLE medicamento_prescrito
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS data_inicio DATE,
    ADD COLUMN IF NOT EXISTS data_fim DATE,
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN;

UPDATE medicamento_prescrito
SET data_inicio = CURRENT_DATE
WHERE data_inicio IS NULL;

UPDATE medicamento_prescrito
SET ativo = TRUE
WHERE ativo IS NULL;

ALTER TABLE medicamento_prescrito
    ALTER COLUMN data_inicio SET NOT NULL;

ALTER TABLE medicamento_prescrito
    ALTER COLUMN ativo SET NOT NULL;

ALTER TABLE medicamento_prescrito
    ALTER COLUMN medicamento_base_id SET NOT NULL;
