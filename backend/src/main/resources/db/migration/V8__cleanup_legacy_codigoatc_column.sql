UPDATE medicamento_base
SET codigo_atc = codigoatc
WHERE codigo_atc IS NULL
  AND codigoatc IS NOT NULL;

ALTER TABLE medicamento_base
    DROP COLUMN IF EXISTS codigoatc;