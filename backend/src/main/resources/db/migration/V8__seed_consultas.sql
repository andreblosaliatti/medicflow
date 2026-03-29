

-- =========================================================
-- CONSULTAS
-- =========================================================
INSERT INTO consultas (
    id,
    data_hora,
    tipo,
    status,
    valor_consulta,
    meio_pagamento,
    pago,
    data_pagamento,
    duracao_minutos,
    retorno,
    data_limite_retorno,
    link_acesso,
    plano_saude,
    numero_carteirinha,
    motivo,
    anamnese,
    exame_fisico,
    diagnostico,
    prescricao,
    observacoes,
    paciente_id,
    medico_id
) VALUES
-- 15 consultas passadas
(1001, '2026-03-03 09:00:00', 'PRESENCIAL',   'CONCLUIDA',      250.00, 'PIX',      true,  '2026-03-03 09:45:00', 45, false, NULL, NULL, 'Unimed',     'UNI-0001', 'Dor de cabeça frequente', 'Cefaleia há 5 dias, pior no fim da tarde, sem vômitos e sem aura.', 'PA 12x8, afebril, sem déficits neurológicos focais.', 'Cefaleia tensional', 'Analgesia se necessário, hidratação e orientação de higiene do sono.', 'Retornar se piora ou surgimento de sinais de alarme.', 1, 2),

(1002, '2026-03-04 10:00:00', 'PRESENCIAL',   'CONCLUIDA',      300.00, 'CREDITO',  true,  '2026-03-04 10:50:00', 50, false, NULL, NULL, 'Bradesco',   'BRA-0002', 'Revisão de exames e acompanhamento metabólico', 'Paciente em acompanhamento clínico de rotina, assintomática.', 'Bom estado geral, sem alterações relevantes ao exame físico.', 'Dislipidemia leve', 'Orientação alimentar, atividade física e acompanhamento laboratorial.', 'Solicitado controle laboratorial.', 2, 2),

(1003, '2026-03-05 11:30:00', 'TELECONSULTA', 'CONCLUIDA',      220.00, 'PIX',      true,  '2026-03-05 12:00:00', 30, false, NULL, 'https://meet.medicflow/consulta-001', 'SulAmérica', 'SUL-0003', 'Sintomas gripais', 'Febre baixa, tosse seca, odinofagia leve e mal-estar há 2 dias.', 'Sem exame físico presencial; sem dispneia ou sinais de alarme referidos.', 'Síndrome gripal', 'Sintomáticos, hidratação, repouso e reavaliação se piora.', 'Teleconsulta de baixa complexidade.', 3, 2),

(1004, '2026-03-06 14:00:00', 'PRESENCIAL',   'CONCLUIDA',      280.00, 'DEBITO',   true,  '2026-03-06 14:40:00', 40, false, NULL, NULL, 'Amil',       'AMI-0004', 'Dor lombar após esforço', 'Dor lombar mecânica iniciada após esforço físico, sem irradiação importante.', 'Dor à palpação paravertebral lombar, sem déficit motor, sem sinais radiculares.', 'Lombalgia mecânica', 'Analgésico, anti-inflamatório por curto período e orientação postural.', 'Solicitar imagem apenas se persistência ou sinais de alarme.', 4, 2),

(1005, '2026-03-07 08:30:00', 'PRESENCIAL',   'CONCLUIDA',      260.00, 'DINHEIRO', true,  '2026-03-07 09:15:00', 45, true,  '2026-04-07 00:00:00', NULL, 'SulAmérica', 'SUL-0005', 'Dor epigástrica e azia', 'Queimação epigástrica recorrente, piora após café e refeições volumosas.', 'Abdome flácido, dor leve à palpação em epigástrio, sem sinais de peritonite.', 'Dispepsia / gastrite', 'IBP por 30 dias, fracionamento alimentar e redução de irritantes gástricos.', 'Programado retorno clínico.', 5, 2),

(1006, '2026-03-10 15:00:00', 'PRESENCIAL',   'CONCLUIDA',      320.00, 'PIX',      true,  '2026-03-10 15:35:00', 35, false, NULL, NULL, 'Unimed',     'UNI-0006', 'Check-up anual', 'Consulta preventiva de rotina, sem queixas agudas.', 'Sem alterações relevantes ao exame clínico geral.', 'Avaliação preventiva', 'Solicitados exames laboratoriais de rotina e orientação de estilo de vida.', 'Seguimento ambulatorial habitual.', 6, 2),

(1007, '2026-03-11 09:45:00', 'TELECONSULTA', 'CONCLUIDA',      210.00, 'PIX',      true,  '2026-03-11 10:15:00', 30, false, NULL, 'https://meet.medicflow/consulta-002', 'Bradesco', 'BRA-0007', 'Insônia', 'Dificuldade para iniciar o sono há cerca de 3 semanas, com despertares frequentes.', 'Sem exame físico presencial.', 'Insônia inicial', 'Higiene do sono, reduzir cafeína e medicação de curto prazo se necessário.', 'Acompanhamento ambulatorial.', 7, 2),

(1008, '2026-03-12 16:00:00', 'PRESENCIAL',   'CANCELADA',      250.00, 'PIX',      false, NULL,                   30, false, NULL, NULL, 'Amil',       'AMI-0008', 'Consulta cancelada', NULL, NULL, NULL, NULL, 'Paciente cancelou no mesmo dia.', 8, 2),

(1009, '2026-03-13 13:30:00', 'PRESENCIAL',   'NAO_COMPARECEU', 250.00, 'DINHEIRO', false, NULL,                   30, false, NULL, NULL, 'Unimed',     'UNI-0009', 'Avaliação clínica', NULL, NULL, NULL, NULL, 'Paciente não compareceu.', 9, 2),

(1010, '2026-03-14 10:30:00', 'URGENCIA',     'CONCLUIDA',      400.00, 'PIX',      true,  '2026-03-14 11:10:00', 40, false, NULL, NULL, 'Bradesco',   'BRA-0010', 'Crise alérgica leve', 'Prurido cutâneo e rinorreia após exposição ambiental, sem dispneia.', 'Sem sibilância, sem edema de glote, estável hemodinamicamente.', 'Reação alérgica leve', 'Antialérgico e orientação para evitar gatilhos.', 'Sem critérios de internação.', 10, 2),

(1011, '2026-03-17 09:00:00', 'PRESENCIAL',   'CONCLUIDA',      250.00, 'DEBITO',   true,  '2026-03-17 09:30:00', 30, true,  '2026-04-01 00:00:00', NULL, 'Unimed', 'UNI-0011', 'Retorno de hipertensão arterial', 'Paciente em acompanhamento, usando medicação regularmente.', 'PA 13x8, ausculta cardíaca sem alterações relevantes.', 'Hipertensão arterial controlada', 'Manter anti-hipertensivo e seguir monitorização clínica.', 'Solicitados exames de função renal e cardiovascular.', 1, 2),

(1012, '2026-03-18 11:00:00', 'PRESENCIAL',   'CONCLUIDA',      280.00, 'CREDITO',  true,  '2026-03-18 11:45:00', 45, false, NULL, NULL, 'Bradesco',   'BRA-0012', 'Dor abdominal em avaliação', 'Dor abdominal difusa leve, sem sinais de abdome agudo.', 'Abdome flácido, ruídos presentes, dor leve difusa sem defesa.', 'Dor abdominal inespecífica', 'Analgesia, hidratação e investigação laboratorial/urinária.', 'Orientado retorno se piora.', 2, 2),

(1013, '2026-03-19 14:30:00', 'PRESENCIAL',   'CONCLUIDA',      250.00, 'PIX',      true,  '2026-03-19 15:20:00', 50, false, NULL, NULL, 'SulAmérica', 'SUL-0013', 'Dor no joelho', 'Dor ao caminhar e subir escadas há 2 semanas.', 'Dor à mobilização do joelho, sem derrame importante.', 'Gonalgia mecânica', 'Analgésico, anti-inflamatório curto e medidas físicas.', 'Reavaliar se persistência.', 3, 2),

(1014, '2026-03-20 08:00:00', 'RETORNO',      'CONCLUIDA',      180.00, 'PIX',      true,  '2026-03-20 08:25:00', 25, false, NULL, NULL, 'Amil',       'AMI-0014', 'Retorno de sinusite/alergia respiratória', 'Melhora parcial, ainda com coriza e congestão nasal.', 'Sem febre, sem desconforto respiratório.', 'Rinite alérgica / melhora clínica', 'Manter sintomáticos e anti-histamínico.', 'Alta do episódio agudo, manter observação.', 4, 2),

(1015, '2026-03-21 17:00:00', 'PRESENCIAL',   'CONCLUIDA',      250.00, 'DINHEIRO', true,  '2026-03-21 17:35:00', 35, false, NULL, NULL, 'SulAmérica', 'SUL-0015', 'Acompanhamento de diabetes e perfil metabólico', 'Paciente em seguimento, refere adesão irregular à dieta.', 'Sem alterações agudas ao exame físico.', 'Diabetes mellitus tipo 2 em acompanhamento', 'Reforço de adesão, metformina e solicitação de controle glicêmico.', 'Acompanhamento clínico continuado.', 5, 2),

-- 10 consultas futuras
(1016, '2026-03-30 08:00:00', 'PRESENCIAL',   'AGENDADA',   250.00, 'PIX',      false, NULL, 30, false, NULL, NULL, 'Unimed',     'UNI-0101', 'Consulta de rotina', NULL, NULL, NULL, NULL, 'Agendamento futuro.', 1, 2),
(1017, '2026-03-30 09:00:00', 'PRESENCIAL',   'CONFIRMADA', 250.00, 'CREDITO',  false, NULL, 30, false, NULL, NULL, 'Bradesco',   'BRA-0102', 'Revisão clínica', NULL, NULL, NULL, NULL, 'Agendamento futuro confirmado.', 2, 2),
(1018, '2026-03-31 10:00:00', 'TELECONSULTA', 'AGENDADA',   220.00, 'PIX',      false, NULL, 30, false, NULL, 'https://meet.medicflow/consulta-101', 'SulAmérica', 'SUL-0103', 'Avaliação de sintomas respiratórios', NULL, NULL, NULL, NULL, 'Teleconsulta agendada.', 3, 2),
(1019, '2026-03-31 11:30:00', 'PRESENCIAL',   'AGENDADA',   260.00, 'DEBITO',   false, NULL, 45, false, NULL, NULL, 'Amil',       'AMI-0104', 'Dor cervical', NULL, NULL, NULL, NULL, 'Agendamento futuro.', 4, 2),
(1020, '2026-04-01 14:00:00', 'RETORNO',      'CONFIRMADA', 180.00, 'PIX',      false, NULL, 25, false, NULL, NULL, 'SulAmérica', 'SUL-0105', 'Retorno de acompanhamento', NULL, NULL, NULL, NULL, 'Retorno futuro.', 5, 2),
(1021, '2026-04-01 15:00:00', 'PRESENCIAL',   'AGENDADA',   300.00, 'DINHEIRO', false, NULL, 40, false, NULL, NULL, 'Unimed',     'UNI-0106', 'Check-up', NULL, NULL, NULL, NULL, 'Consulta preventiva agendada.', 6, 2),
(1022, '2026-04-02 08:30:00', 'PRESENCIAL',   'CONFIRMADA', 250.00, 'PIX',      false, NULL, 30, false, NULL, NULL, 'Bradesco',   'BRA-0107', 'Controle de pressão arterial', NULL, NULL, NULL, NULL, 'Consulta confirmada.', 7, 2),
(1023, '2026-04-02 10:30:00', 'TELECONSULTA', 'AGENDADA',   220.00, 'PIX',      false, NULL, 30, false, NULL, 'https://meet.medicflow/consulta-102', 'Amil', 'AMI-0108', 'Discussão de exames', NULL, NULL, NULL, NULL, 'Teleconsulta futura.', 8, 2),
(1024, '2026-04-03 13:30:00', 'URGENCIA',     'AGENDADA',   400.00, 'PIX',      false, NULL, 40, false, NULL, NULL, 'Unimed',     'UNI-0109', 'Encaixe por piora clínica', NULL, NULL, NULL, NULL, 'Encaixe de urgência.', 9, 2),
(1025, '2026-04-03 16:00:00', 'PRESENCIAL',   'CONFIRMADA', 250.00, 'CREDITO',  false, NULL, 30, false, NULL, NULL, 'SulAmérica', 'SUL-0110', 'Avaliação dermatológica inicial', NULL, NULL, NULL, NULL, 'Consulta futura confirmada.', 10, 2);

-- =========================================================
-- MEDICAMENTOS PRESCRITOS (consultas passadas)
-- =========================================================
INSERT INTO medicamento_prescrito (
    nome,
    dosagem,
    frequencia,
    via,
    observacoes,
    data_inicio,
    data_fim,
    ativo,
    consulta_id,
    medicamento_base_id
) VALUES
('Dipirona',    '500 mg', '1 comprimido a cada 6 horas se dor', 'Oral', 'Usar apenas se necessário.', '2026-03-03', '2026-03-06', false, 1001, 301),
('Paracetamol', '750 mg', '1 comprimido a cada 8 horas se dor', 'Oral', 'Alternativa analgésica se necessário.', '2026-03-03', '2026-03-05', false, 1001, 651),

('Sinvastatina','20 mg', '1 comprimido à noite', 'Oral', 'Uso contínuo conforme orientação médica.', '2026-03-04', NULL, true, 1002, 501),

('Paracetamol', '750 mg', '1 comprimido a cada 8 horas se febre ou dor', 'Oral', 'Hidratação e repouso associados.', '2026-03-05', '2026-03-08', false, 1003, 651),
('Loratadina',  '10 mg', '1 comprimido ao dia', 'Oral', 'Para controle de sintomas de vias aéreas superiores.', '2026-03-05', '2026-03-10', false, 1003, 351),

('Ibuprofeno',  '600 mg', '1 comprimido a cada 12 horas após refeições', 'Oral', 'Uso curto, suspender se dor gástrica.', '2026-03-06', '2026-03-10', false, 1004, 201),
('Dipirona',    '500 mg', '1 comprimido a cada 6 horas se dor', 'Oral', 'Analgésico de resgate.', '2026-03-06', '2026-03-09', false, 1004, 301),

('Omeprazol',   '20 mg', '1 cápsula em jejum pela manhã', 'Oral', 'Evitar café, álcool e refeições pesadas.', '2026-03-07', '2026-04-06', true, 1005, 1),

('Diazepam',    '5 mg', '1 comprimido à noite', 'Oral', 'Uso por curto prazo, avaliar suspensão.', '2026-03-11', '2026-03-18', false, 1007, 551),

('Loratadina',  '10 mg', '1 comprimido ao dia', 'Oral', 'Uso por 7 dias.', '2026-03-14', '2026-03-21', false, 1010, 351),
('Prednisona',  '20 mg', '1 comprimido ao dia por 5 dias', 'Oral', 'Reavaliar se persistirem sintomas.', '2026-03-14', '2026-03-18', false, 1010, 601),

('Losartana',   '50 mg', '1 comprimido ao dia', 'Oral', 'Manter uso contínuo.', '2026-03-17', NULL, true, 1011, 101),

('Dipirona',    '500 mg', '1 comprimido a cada 6 horas se dor', 'Oral', 'Retornar se piora.', '2026-03-18', '2026-03-20', false, 1012, 301),

('Ibuprofeno',  '400 mg', '1 comprimido a cada 12 horas após refeições', 'Oral', 'Uso curto para dor mecânica.', '2026-03-19', '2026-03-23', false, 1013, 201),

('Loratadina',  '10 mg', '1 comprimido ao dia', 'Oral', 'Controle de rinite/alergia.', '2026-03-20', '2026-03-27', false, 1014, 351),

('Metformina',  '500 mg', '1 comprimido após o jantar', 'Oral', 'Reforçar adesão dietética.', '2026-03-21', NULL, true, 1015, 251);

-- =========================================================
-- EXAMES SOLICITADOS (consultas passadas)
-- =========================================================
INSERT INTO exame_solicitado (
    status,
    justificativa,
    observacoes,
    data_coleta,
    data_resultado,
    consulta_id,
    exame_base_id
) VALUES
('REALIZADO', 'Cefaleia persistente sem sinais neurológicos, investigação básica.', 'Exame sem alterações relevantes registradas.', '2026-03-04 08:00:00', '2026-03-04 17:00:00', 1001, 12),
('REALIZADO', 'Revisão metabólica e perfil lipídico.', 'Utilizado para seguimento clínico.', '2026-03-05 07:30:00', '2026-03-05 16:00:00', 1002, 16),
('REALIZADO', 'Avaliação complementar do perfil lipídico.', 'Triglicerídeos discretamente elevados.', '2026-03-05 07:30:00', '2026-03-05 16:00:00', 1002, 8),

('SOLICITADO', 'Solicitado apenas se persistência da dor lombar ou sinais de alarme.', 'Paciente inicialmente em tratamento conservador.', NULL, NULL, 1004, 19),

('AGENDADO', 'Investigação de queixas dispépticas persistentes.', 'Exame eletivo ambulatorial.', '2026-03-25 09:00:00', NULL, 1005, 1),

('REALIZADO', 'Check-up laboratorial de rotina.', 'Exame de triagem.', '2026-03-11 07:00:00', '2026-03-11 15:30:00', 1006, 12),
('REALIZADO', 'Avaliação metabólica preventiva.', 'Glicemia sem intercorrências.', '2026-03-11 07:00:00', '2026-03-11 15:30:00', 1006, 18),
('REALIZADO', 'Função renal em avaliação preventiva.', 'Creatinina dentro do esperado.', '2026-03-11 07:00:00', '2026-03-11 15:30:00', 1006, 20),

('REALIZADO', 'Acompanhamento de hipertensão arterial.', 'Sem alterações importantes.', '2026-03-18 08:00:00', '2026-03-18 16:00:00', 1011, 20),
('REALIZADO', 'Avaliação de função renal em paciente hipertenso.', 'Ureia sem alterações significativas.', '2026-03-18 08:00:00', '2026-03-18 16:00:00', 1011, 7),
('REALIZADO', 'Avaliação cardiovascular complementar.', 'Exame sem intercorrências.', '2026-03-19 10:00:00', '2026-03-19 18:00:00', 1011, 15),

('REALIZADO', 'Investigação inicial de dor abdominal.', 'Exame de urina disponível no mesmo dia.', '2026-03-18 13:00:00', '2026-03-18 18:00:00', 1012, 11),
('SOLICITADO', 'Pesquisar infecção urinária se evolução clínica sugerir.', 'Solicitado de forma complementar.', NULL, NULL, 1012, 4),
('AGENDADO', 'Ultrassonografia abdominal em investigação de dor persistente.', 'Agendado em caráter ambulatorial.', '2026-03-24 11:00:00', NULL, 1012, 3),

('REALIZADO', 'Controle glicêmico em paciente com DM2.', 'Exame laboratorial de rotina.', '2026-03-24 07:00:00', '2026-03-24 14:30:00', 1015, 18),
('REALIZADO', 'Avaliação metabólica de seguimento.', 'Hemograma solicitado no contexto de check clínico geral.', '2026-03-24 07:00:00', '2026-03-24 14:30:00', 1015, 12);

SELECT setval(
    pg_get_serial_sequence('consultas', 'id'),
    COALESCE((SELECT MAX(id) FROM consultas), 1),
    true
);

SELECT setval(
    pg_get_serial_sequence('medicamento_prescrito', 'id'),
    COALESCE((SELECT MAX(id) FROM medicamento_prescrito), 1),
    true
);

SELECT setval(
    pg_get_serial_sequence('exame_solicitado', 'id'),
    COALESCE((SELECT MAX(id) FROM exame_solicitado), 1),
    true
);

