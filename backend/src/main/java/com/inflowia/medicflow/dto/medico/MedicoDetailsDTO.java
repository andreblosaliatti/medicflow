package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.security.AccessRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String crm;
    private String especialidade;
    private String instituicaoFormacao;
    private LocalDate dataFormacao;
    private String sexo;
    private String observacoes;
    private Set<String> roles;
    private boolean ativo;
    private MedicoDTO.EnderecoDTO endereco;

    public MedicoDetailsDTO(Medico entity) {
        this.id = entity.getId();
        this.login = entity.getLogin();
        this.nome = entity.getNome();
        this.sobrenome = entity.getSobrenome();
        this.nomeCompleto = buildNomeCompleto(entity.getNome(), entity.getSobrenome());
        this.email = entity.getEmail();
        this.cpf = entity.getCpf();
        this.crm = entity.getCrm();
        this.especialidade = entity.getEspecialidade();
        this.instituicaoFormacao = entity.getInstituicaoFormacao();
        this.dataFormacao = entity.getDataFormacao();
        this.sexo = entity.getSexo();
        this.observacoes = entity.getObservacoes();
        this.roles = new LinkedHashSet<>(AccessRole.toApiNames(entity.getRoles().stream().map(role -> role.getAuthority()).toList()));
        this.ativo = entity.isAtivo();
        this.endereco = MedicoDTO.EnderecoDTO.fromEntity(entity.getEndereco());
    }

    private static String buildNomeCompleto(String nome, String sobrenome) {
        return (nome + " " + (sobrenome == null ? "" : sobrenome)).trim();
    }
}
