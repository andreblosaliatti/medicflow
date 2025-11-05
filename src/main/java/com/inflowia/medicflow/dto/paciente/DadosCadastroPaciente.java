package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.dto.DadosEndereco;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

/**
 * DTO usado para cadastro de um novo paciente no sistema.
 */
public record DadosCadastroPaciente(
        @NotBlank(message = "Nome é obrigatório.")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf,

        @NotNull(message = "Data de nascimento é obrigatória.")
        LocalDate dataNascimento,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Formato de telefone inválido. Ex: (51) 99999-9999")
        String telefone,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotNull(message = "Endereço é obrigatório")
        DadosEndereco endereco

) {}
