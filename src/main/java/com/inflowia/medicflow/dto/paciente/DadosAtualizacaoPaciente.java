package com.inflowia.medicflow.dto.paciente;

import com.inflowia.medicflow.dto.DadosEndereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record DadosAtualizacaoPaciente(
    @NotNull
    Long id,

    String nome,

    @CPF(message = "CPF inválido")
    String cpf,

    LocalDate dataNascimento,

    @Pattern(regexp = "\\(\\d{2} \\d{4,5}-\\d{4}", message = "Formato de telefone inválido. Ex: (51) 99999-9999")
    String telefone,

    @Email(message = "E-mail inválido")
    String email,

    DadosEndereco endereco

){}

