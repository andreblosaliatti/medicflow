package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.entities.usuario.Medico;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoDetailsDTO {

    private Long id;
    private String login;
    private String nome;
    private String sobrenome;
    private String email;
    private String cpf;
    private String crm;
    private String especialidade;
    private String instituicaoFormacao;
    private LocalDate dataFormacao;
    private String sexo;
    private String observacoes;
    private boolean ativo;

    private MedicoDTO.EnderecoDTO endereco;

    public MedicoDetailsDTO(Medico entity) {
        this.id = entity.getId();
        this.login = entity.getLogin();
        this.nome = entity.getNome();
        this.sobrenome = entity.getSobrenome();
        this.email = entity.getEmail();
        this.cpf = entity.getCpf();
        this.crm = entity.getCrm();
        this.especialidade = entity.getEspecialidade();
        this.instituicaoFormacao = entity.getInstituicaoFormacao();
        this.dataFormacao = entity.getDataFormacao();
        this.sexo = entity.getSexo();
        this.observacoes = entity.getObservacoes();
        this.ativo = entity.isAtivo();
        this.endereco = MedicoDTO.EnderecoDTO.fromEntity(entity.getEndereco());
    }
}
