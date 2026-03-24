BEGIN;

INSERT INTO pacientes
(
    ativo,
    data_nascimento,
    nome,
    sobrenome,
    cpf,
    telefone,
    email,
    logradouro,
    numero,
    complemento,
    bairro,
    cidade,
    uf,
    cep,
    plano_saude,
    sexo
)
SELECT
    v.ativo,
    v.data_nascimento,
    v.nome,
    v.sobrenome,
    v.cpf,
    v.telefone,
    v.email,
    v.logradouro,
    v.numero,
    v.complemento,
    v.bairro,
    v.cidade,
    v.uf,
    v.cep,
    v.plano_saude,
    v.sexo
FROM (
    VALUES
        (TRUE, DATE '1985-04-12', 'Carlos',   'Santos',   '39053344705', '(51) 99888-1234', 'carlos.santos@example.com',   'Rua das Flores', '120',  'Apto 301', 'Centro',           'Porto Alegre', 'RS', '90560-001', 'Unimed',     'M'),
        (TRUE, DATE '1992-11-23', 'Mariana',  'Costa',    '29537914806', '(51) 98444-5678', 'mariana.costa@example.com',  'Av. Ipiranga',   '4500', NULL,       'Azenha',           'Porto Alegre', 'RS', '90610-000', 'Cassi',      'F'),
        (TRUE, DATE '1978-06-05', 'Ricardo',  'Almeida',  '52998224725', '(51) 99777-2222', 'ricardo.almeida@example.com', 'Rua da Praia',   '55',   'Casa 2',   'Centro Histórico', 'Porto Alegre', 'RS', '90010-000', 'Particular', 'M'),
        (TRUE, DATE '1990-01-15', 'Ana',      'Silva',    '16899535009', '(51) 99111-0001', 'ana.silva@example.com',      'Rua A',          '10',   NULL,       'Bela Vista',       'Porto Alegre', 'RS', '90460-040', 'Unimed',     'F'),
        (TRUE, DATE '1987-03-22', 'Bruno',    'Teixeira', '35719278060', '(51) 99222-0002', 'bruno.teixeira@example.com', 'Rua B',          '20',   NULL,       'Rio Branco',       'Porto Alegre', 'RS', '90420-050', 'Cassi',      'M'),
        (TRUE, DATE '1995-07-09', 'Camila',   'Rocha',    '74697131401', '(51) 99333-0003', 'camila.rocha@example.com',   'Rua C',          '30',   'Sala 5',   'Moinhos de Vento', 'Porto Alegre', 'RS', '90570-060', 'Particular', 'F'),
        (TRUE, DATE '1982-11-30', 'Diego',    'Pereira',  '86288366006', '(51) 99444-0004', 'diego.pereira@example.com',  'Rua D',          '40',   NULL,       'Petrópolis',       'Porto Alegre', 'RS', '90450-070', 'IPÊ Saúde',  'M'),
        (TRUE, DATE '1999-05-18', 'Eduarda',  'Melo',     '40672026004', '(51) 99555-0005', 'eduarda.melo@example.com',   'Rua E',          '50',   'Apto 101', 'Tristeza',         'Porto Alegre', 'RS', '91910-080', 'Unimed',     'F'),
        (TRUE, DATE '1984-09-03', 'Felipe',   'Gomes',    '23100257000', '(51) 99666-0006', 'felipe.gomes@example.com',   'Rua F',          '60',   NULL,       'Cidade Baixa',     'Porto Alegre', 'RS', '90050-090', 'Cassi',      'M'),
        (TRUE, DATE '1975-12-25', 'Gabriela', 'Souza',    '70548445052', '(51) 99777-0007', 'gabriela.souza@example.com', 'Rua G',          '70',   'Casa',     'Menino Deus',      'Porto Alegre', 'RS', '90130-100', 'Particular', 'F')
) AS v(
    ativo,
    data_nascimento,
    nome,
    sobrenome,
    cpf,
    telefone,
    email,
    logradouro,
    numero,
    complemento,
    bairro,
    cidade,
    uf,
    cep,
    plano_saude,
    sexo
)
WHERE NOT EXISTS (
    SELECT 1
    FROM pacientes p
    WHERE p.cpf = v.cpf
);

COMMIT;