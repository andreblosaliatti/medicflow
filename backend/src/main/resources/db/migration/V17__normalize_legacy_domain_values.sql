UPDATE tb_role
SET authority = 'ROLE_SECRETARIA'
WHERE UPPER(TRIM(authority)) IN ('ROLE_SECRETARIO', 'SECRETARIO', 'SECRETARIA');

UPDATE consultas
SET status = 'EM_ATENDIMENTO'
WHERE UPPER(TRIM(status)) = 'EM_ANDAMENTO';

UPDATE consultas
SET status = 'CONCLUIDA'
WHERE UPPER(TRIM(status)) = 'REALIZADA';

UPDATE consultas
SET meio_pagamento = 'DEBITO'
WHERE UPPER(TRIM(meio_pagamento)) IN ('DEBIT', 'CARTAO_DEBITO');

UPDATE consultas
SET meio_pagamento = 'CREDITO'
WHERE UPPER(TRIM(meio_pagamento)) IN ('CREDIT', 'CARTAO', 'CARTAO_CREDITO');

UPDATE consultas
SET meio_pagamento = 'DINHEIRO'
WHERE UPPER(TRIM(meio_pagamento)) = 'CASH';
