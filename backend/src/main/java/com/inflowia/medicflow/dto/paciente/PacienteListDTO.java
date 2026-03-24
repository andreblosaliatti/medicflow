package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.paciente.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(name = "PacienteListDTO", description = "DTO de listagem resumida de pacientes.")
public class PacienteListDTO {

    private final Long id;
    private final String nomeCompleto;
    private final String cpf;
    private final String telefone;
    private final String planoSaude;
    private final boolean ativo;
    private final LocalDateTime ultimaConsulta;

    public PacienteListDTO(Paciente paciente, LocalDateTime ultimaConsulta) {
        String primeiroNome = paciente.getPrimeiroNome() != null ? paciente.getPrimeiroNome() : "";
        String sobrenome = paciente.getSobrenome() != null ? paciente.getSobrenome() : "";

        this.id = paciente.getId();
        this.nomeCompleto = (primeiroNome + " " + sobrenome).trim();
        this.cpf = paciente.getCpf();
        this.telefone = paciente.getTelefone();
        this.planoSaude = paciente.getPlanoSaude();
        this.ativo = paciente.isAtivo();
        this.ultimaConsulta = ultimaConsulta;
    }
}