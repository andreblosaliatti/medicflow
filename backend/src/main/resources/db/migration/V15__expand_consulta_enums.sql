ALTER TABLE consultas
    DROP CONSTRAINT IF EXISTS consultas_meio_pagamento_check,
    DROP CONSTRAINT IF EXISTS consultas_status_check;

ALTER TABLE consultas
    ADD CONSTRAINT consultas_meio_pagamento_check CHECK (meio_pagamento IN ('DEBITO', 'CREDITO', 'PIX', 'DINHEIRO')),
    ADD CONSTRAINT consultas_status_check CHECK (status IN ('AGENDADA', 'CONFIRMADA', 'EM_ATENDIMENTO', 'CONCLUIDA', 'CANCELADA', 'NAO_COMPARECEU'));
