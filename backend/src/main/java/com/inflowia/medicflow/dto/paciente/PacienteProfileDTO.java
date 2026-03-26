package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.dto.EnderecoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(name = "PacienteDetailDTO", description = "DTO de detalhe/perfil de paciente para leitura.")
public class PacienteProfileDTO {

    private final Long id;
    private final String nomeCompleto;
    private final String cpf;
    private final LocalDate dataNascimento;
    private final String telefone;
    private final String email;
    private final String sexo;
    private final String planoSaude;
    private final boolean ativo;
    private final EnderecoDTO endereco;
    private final PacienteHistoricoResumoDTO historico;

    public PacienteProfileDTO(Paciente paciente, PacienteHistoricoResumoDTO historico) {
        this.id = paciente.getId();
        this.nomeCompleto = (paciente.getNome() + " " + paciente.getSobrenome()).trim();
        this.cpf = paciente.getCpf();
        this.dataNascimento = paciente.getDataNascimento();
        this.telefone = paciente.getTelefone();
        this.email = paciente.getEmail();
        this.sexo = paciente.getSexo();
        this.planoSaude = paciente.getPlanoSaude();
        this.ativo = paciente.isAtivo();
        this.endereco = paciente.getEndereco() != null ? new EnderecoDTO(paciente.getEndereco()) : null;
        this.historico = historico;
    }
}
