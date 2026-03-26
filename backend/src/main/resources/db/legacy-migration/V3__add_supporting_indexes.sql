CREATE INDEX IF NOT EXISTS idx_consultas_medico_id
    ON consultas (medico_id);

CREATE INDEX IF NOT EXISTS idx_consultas_paciente_id
    ON consultas (paciente_id);

CREATE INDEX IF NOT EXISTS idx_consultas_data_hora
    ON consultas (data_hora);

CREATE INDEX IF NOT EXISTS idx_consultas_status
    ON consultas (status);

CREATE INDEX IF NOT EXISTS idx_consultas_medico_data_hora
    ON consultas (medico_id, data_hora);

CREATE INDEX IF NOT EXISTS idx_consultas_paciente_data_hora
    ON consultas (paciente_id, data_hora);

CREATE INDEX IF NOT EXISTS idx_exame_solicitado_consulta_id
    ON exame_solicitado (consulta_id);

CREATE INDEX IF NOT EXISTS idx_exame_solicitado_exame_base_id
    ON exame_solicitado (exame_base_id);

CREATE INDEX IF NOT EXISTS idx_exame_solicitado_status
    ON exame_solicitado (status);

CREATE INDEX IF NOT EXISTS idx_medicamento_prescrito_consulta_id
    ON medicamento_prescrito (consulta_id);

CREATE INDEX IF NOT EXISTS idx_medicamento_prescrito_paciente_id
    ON medicamento_prescrito (paciente_id);

CREATE INDEX IF NOT EXISTS idx_medicamento_prescrito_medicamento_base_id
    ON medicamento_prescrito (medicamento_base_id);

CREATE INDEX IF NOT EXISTS idx_tb_usuario_role_usuario_id
    ON tb_usuario_role (usuario_id);

CREATE INDEX IF NOT EXISTS idx_tb_usuario_role_role_id
    ON tb_usuario_role (role_id);