DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'medicamento_base'
          AND column_name = 'dosagem_padrão'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'medicamento_base'
          AND column_name = 'dosagem_padrao'
    ) THEN
        ALTER TABLE medicamento_base
            RENAME COLUMN "dosagem_padrão" TO dosagem_padrao;
    END IF;
END $$;

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS dosagem_padrao varchar(120);