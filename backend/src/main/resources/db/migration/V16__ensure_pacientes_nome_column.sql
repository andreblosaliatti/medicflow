DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'pacientes'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'pacientes'
          AND column_name = 'nome'
    ) THEN
        ALTER TABLE pacientes ADD COLUMN nome VARCHAR(255);

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'pacientes'
              AND column_name = 'primeiro_nome'
        ) THEN
            EXECUTE 'UPDATE pacientes SET nome = primeiro_nome WHERE nome IS NULL';
        END IF;
    END IF;
END $$;
