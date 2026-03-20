-- =========================================
-- 1. Garantir dados consistentes
-- =========================================

UPDATE medicamento_prescrito
SET
    nome_prescrito = COALESCE(nome_prescrito, 'DESCONHECIDO'),
    dosagem_prescrita = COALESCE(dosagem_prescrita, 'N/A'),
    ativo = COALESCE(ativo, TRUE),
    data_inicio = COALESCE(data_inicio, CURRENT_DATE);

-- =========================================
-- 2. Corrigir registros sem medicamento_base
-- (CRÍTICO: evita falha de NOT NULL)
-- =========================================

-- Verifica antes (debug manual recomendado)
-- SELECT * FROM medicamento_prescrito WHERE medicamento_base_id IS NULL;

-- Se existir, você tem 2 opções:
-- OPÇÃO A (segura): remover registros inválidos
DELETE FROM medicamento_prescrito
WHERE medicamento_base_id IS NULL;

-- OPÇÃO B (alternativa): criar fallback (não recomendado em sistema clínico)
-- UPDATE medicamento_prescrito SET medicamento_base_id = <ID_PADRAO>

-- =========================================
-- 3. Garantir constraints
-- =========================================

ALTER TABLE medicamento_prescrito
ALTER COLUMN nome_prescrito SET NOT NULL,
ALTER COLUMN dosagem_prescrita SET NOT NULL,
ALTER COLUMN ativo SET NOT NULL;

-- =========================================
-- 4. Índices (performance)
-- =========================================

CREATE INDEX IF NOT EXISTS idx_medicamento_prescrito_consulta_ativo
ON medicamento_prescrito (consulta_id, ativo);

CREATE INDEX IF NOT EXISTS idx_medicamento_prescrito_base
ON medicamento_prescrito (medicamento_base_id);

-- =========================================
-- 5. (Opcional, mas recomendado)
-- Preparar para consultas futuras
-- =========================================

CREATE INDEX IF NOT EXISTS idx_medicamento_prescrito_data
ON medicamento_prescrito (data_inicio, data_fim);