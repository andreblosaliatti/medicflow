ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS concentracao varchar(120);

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS registro_anvisa varchar(50);

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS fabricante varchar(255);

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS controlado boolean NOT NULL DEFAULT false;

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS ativo boolean NOT NULL DEFAULT true;

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS codigo_atc varchar(50);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'medicamento_base'
          AND column_name = 'codigoatc'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'medicamento_base'
          AND column_name = 'codigo_atc'
    ) THEN
        ALTER TABLE medicamento_base RENAME COLUMN codigoatc TO codigo_atc;
    END IF;
END $$;