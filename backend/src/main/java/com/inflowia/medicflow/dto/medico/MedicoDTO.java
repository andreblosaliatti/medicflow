package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.domain.usuario.Medico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoDTO {

    @NotBlank
    private String login;

    @NotBlank
    @Size(min = 6, max = 100)
    private String senha;

    @NotBlank
    private String nome;

    @NotBlank
    private String sobrenome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @CPF
    private String cpf;

    @NotBlank
    private String crm;

    @NotBlank
    private String especialidade;

    private String instituicaoFormacao;

    private LocalDate dataFormacao;

    private String sexo;

    private String observacoes;

    @Valid
    private EnderecoDTO endereco;

    @NotEmpty
    @Builder.Default
    private Set<String> roles = new LinkedHashSet<>(Set.of("MEDICO"));

    public Medico toEntity() {
        Medico medico = new Medico();

        medico.setLogin(login);
        medico.setSenha(senha);
        medico.setNome(nome);
        medico.setSobrenome(sobrenome);
        medico.setEmail(email);
        medico.setCpf(cpf);
        medico.setCrm(crm);
        medico.setEspecialidade(especialidade);
        medico.setInstituicaoFormacao(instituicaoFormacao);
        medico.setDataFormacao(dataFormacao);
        medico.setSexo(sexo);
        medico.setObservacoes(observacoes);

        if (endereco != null) {
            medico.setEndereco(endereco.toEntity());
        }

        medico.setAtivo(true);
        return medico;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnderecoDTO {

        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String uf;
        private String cep;

        public com.inflowia.medicflow.domain.paciente.Endereco toEntity() {
            com.inflowia.medicflow.domain.paciente.Endereco e = new com.inflowia.medicflow.domain.paciente.Endereco();
            e.setLogradouro(logradouro);
            e.setNumero(numero);
            e.setComplemento(complemento);
            e.setBairro(bairro);
            e.setCidade(cidade);
            e.setUf(uf);
            e.setCep(cep);
            return e;
        }

        public static EnderecoDTO fromEntity(com.inflowia.medicflow.domain.paciente.Endereco endereco) {
            if (endereco == null) return null;
            return EnderecoDTO.builder()
                    .logradouro(endereco.getLogradouro())
                    .numero(endereco.getNumero())
                    .complemento(endereco.getComplemento())
                    .bairro(endereco.getBairro())
                    .cidade(endereco.getCidade())
                    .uf(endereco.getUf())
                    .cep(endereco.getCep())
                    .build();
        }
    }
}
