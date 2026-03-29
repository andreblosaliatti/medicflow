

INSERT INTO pacientes (
    nome,
    sobrenome,
    cpf,
    data_nascimento,
    telefone,
    email,
    sexo,
    logradouro,
    numero,
    complemento,
    bairro,
    cidade,
    uf,
    cep,
    plano_saude,
    ativo
)
SELECT * FROM (
    VALUES
    ('João', 'Silva', '52998224725', DATE '1985-03-15', '11999990001', 'joao.silva@email.com', 'M', 'Rua A', '100', NULL, 'Centro', 'São Paulo', 'SP', '01001000', 'Unimed', true),

    ('Maria', 'Souza', '11144477735', DATE '1990-07-22', '11999990002', 'maria.souza@email.com', 'F', 'Rua B', '200', 'Apto 12', 'Jardins', 'São Paulo', 'SP', '01415000', 'Bradesco', true),

    ('Carlos', 'Oliveira', '93541134780', DATE '1978-11-05', '11999990003', 'carlos.oliveira@email.com', 'M', 'Rua C', '300', NULL, 'Moema', 'São Paulo', 'SP', '04077000', 'SulAmérica', true),

    ('Ana', 'Costa', '28625587887', DATE '1988-01-30', '11999990004', 'ana.costa@email.com', 'F', 'Rua D', '400', NULL, 'Pinheiros', 'São Paulo', 'SP', '05422000', 'Amil', true),

    ('Pedro', 'Santos', '16899535009', DATE '1995-06-18', '11999990005', 'pedro.santos@email.com', 'M', 'Rua E', '500', NULL, 'Vila Mariana', 'São Paulo', 'SP', '04117000', 'Unimed', true),

    ('Juliana', 'Pereira', '39021544806', DATE '1992-09-10', '11999990006', 'juliana.pereira@email.com', 'F', 'Rua F', '600', NULL, 'Tatuapé', 'São Paulo', 'SP', '03063000', 'Bradesco', true),

    ('Marcos', 'Ferreira', '61579058079', DATE '1980-04-25', '11999990007', 'marcos.ferreira@email.com', 'M', 'Rua G', '700', NULL, 'Santana', 'São Paulo', 'SP', '02018000', 'SulAmérica', true),

    ('Fernanda', 'Almeida', '82402514000', DATE '1987-12-12', '11999990008', 'fernanda.almeida@email.com', 'F', 'Rua H', '800', NULL, 'Ipiranga', 'São Paulo', 'SP', '04266000', 'Amil', true),

    ('Ricardo', 'Gomes', '02701547004', DATE '1975-08-03', '11999990009', 'ricardo.gomes@email.com', 'M', 'Rua I', '900', NULL, 'Lapa', 'São Paulo', 'SP', '05065000', 'Unimed', true),

    ('Camila', 'Martins', '38216814009', DATE '1998-02-27', '11999990010', 'camila.martins@email.com', 'F', 'Rua J', '1000', NULL, 'Butantã', 'São Paulo', 'SP', '05508000', 'Bradesco', true)

) AS t(
    nome,
    sobrenome,
    cpf,
    data_nascimento,
    telefone,
    email,
    sexo,
    logradouro,
    numero,
    complemento,
    bairro,
    cidade,
    uf,
    cep,
    plano_saude,
    ativo
)
WHERE NOT EXISTS (
    SELECT 1
    FROM pacientes p
    WHERE p.cpf = t.cpf
);

