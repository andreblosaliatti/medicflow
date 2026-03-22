package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.dto.EnderecoDTO;
import com.inflowia.medicflow.domain.paciente.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PacienteFormDTO", description = "DTO de formulário para cadastro completo de pacientes.")
public class PacienteDTO {

    private Long id;

    @NotBlank(message = "Primeiro nome é obrigatório")
    private String primeiroNome;

    @NotBlank(message = "Sobrenome é obrigatório")
    private String sobrenome;

    @CPF
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}",
            message = "Formato esperado: (99) 99999-9999")
    private String telefone;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    @NotBlank(message = "Sexo é obrigatório")
    private String sexo;

    @NotNull(message = "Endereço é obrigatório")
    private EnderecoDTO endereco;

    private String planoSaude;

    private boolean ativo;

    public PacienteDTO(Paciente p) {
        this.id = p.getId();
        this.primeiroNome = p.getPrimeiroNome();
        this.sobrenome = p.getSobrenome();
        this.cpf = p.getCpf();
        this.dataNascimento = p.getDataNascimento();
        this.telefone = p.getTelefone();
        this.email = p.getEmail();
        this.sexo = p.getSexo();
        this.endereco = p.getEndereco() != null ? new EnderecoDTO(p.getEndereco()) : null;
        this.planoSaude = p.getPlanoSaude();
        this.ativo = p.isAtivo();
    }
}
