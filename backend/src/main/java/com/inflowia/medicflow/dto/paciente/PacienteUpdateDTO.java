package com.inflowia.medicflow.dto.paciente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.inflowia.medicflow.dto.EnderecoDTO;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PacienteUpdateFormDTO", description = "DTO de formulário para atualização parcial/total de pacientes.")
public class PacienteUpdateDTO {

    private String nome;
    private String sobrenome;

    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}",
            message = "Formato esperado: (99) 99999-9999")
    private String telefone;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    private String sexo;

    private LocalDate dataNascimento;

    private EnderecoDTO endereco;

    private String planoSaude;
}
