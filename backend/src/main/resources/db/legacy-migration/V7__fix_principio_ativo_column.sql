DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'medicamento_base'
          AND column_name = 'pricipio_ativo'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'medicamento_base'
          AND column_name = 'principio_ativo'
    ) THEN
        ALTER TABLE medicamento_base
            RENAME COLUMN pricipio_ativo TO principio_ativo;
    END IF;
END $$;

ALTER TABLE medicamento_base
    ADD COLUMN IF NOT EXISTS principio_ativo varchar(255);
