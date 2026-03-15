package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.entities.paciente.Endereco;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoUpdateDTO {

    private String nome;
    private String sobrenome;

    @Email
    private String email;

    private String telefone; // se vier a existir em Usuario

    private String especialidade;
    private String instituicaoFormacao;
    private LocalDate dataFormacao;
    private String sexo;
    private String observacoes;

    private MedicoDTO.EnderecoDTO endereco;

    public Endereco toEnderecoEntity() {
        if (endereco == null) return null;
        return endereco.toEntity();
    }
}
