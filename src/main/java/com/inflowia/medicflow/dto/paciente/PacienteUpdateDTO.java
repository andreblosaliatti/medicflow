package com.inflowia.medicflow.dto.paciente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.inflowia.medicflow.dto.EnderecoDTO;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteUpdateDTO {

    private String primeiroNome;
    private String sobrenome;

    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}",
            message = "Formato esperado: (99) 99999-9999")
    private String telefone;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    private String sexo;

    private LocalDate dataNascimento;

    private EnderecoDTO endereco;
}
