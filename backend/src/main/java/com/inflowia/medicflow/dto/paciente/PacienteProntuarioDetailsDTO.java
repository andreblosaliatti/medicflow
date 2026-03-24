package com.inflowia.medicflow.dto.paciente;

import java.time.LocalDate;
import java.util.List;

public class PacienteProntuarioDetailsDTO {

    private final Long pacienteId;
    private final String pacienteNome;
    private final String documento;
    private final LocalDate dataNascimento;
    private final List<PacienteProntuarioConsultaDTO> consultas;

    public PacienteProntuarioDetailsDTO(Long pacienteId,
                                        String pacienteNome,
                                        String documento,
                                        LocalDate dataNascimento,
                                        List<PacienteProntuarioConsultaDTO> consultas) {
        this.pacienteId = pacienteId;
        this.pacienteNome = pacienteNome;
        this.documento = documento;
        this.dataNascimento = dataNascimento;
        this.consultas = consultas;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public String getDocumento() {
        return documento;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public List<PacienteProntuarioConsultaDTO> getConsultas() {
        return consultas;
    }
}
