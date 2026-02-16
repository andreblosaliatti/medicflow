package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.entities.paciente.Endereco;
import com.inflowia.medicflow.entities.usuario.Medico;
import com.inflowia.medicflow.entities.usuario.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
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
    private String senha;

    @NotBlank
    private String nome;

    @NotBlank
    private String sobrenome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String cpf;

    @NotBlank
    private String crm;

    @NotBlank
    private String especialidade;

    private String instituicaoFormacao;

    private LocalDate dataFormacao;

    private String sexo;

    private String observacoes;

    private EnderecoDTO endereco;

    // Agora o DTO aceita roles também
    private Set<Role> roles;

    public Medico toEntity() {
        Medico medico = new Medico();

        medico.setLogin(this.login);
        medico.setSenha(this.senha);
        medico.setNome(this.nome);
        medico.setSobrenome(this.sobrenome);
        medico.setEmail(this.email);
        medico.setCpf(this.cpf);

        medico.setCrm(this.crm);
        medico.setEspecialidade(this.especialidade);
        medico.setInstituicaoFormacao(this.instituicaoFormacao);
        medico.setDataFormacao(this.dataFormacao);
        medico.setSexo(this.sexo);
        medico.setObservacoes(this.observacoes);

        if (this.endereco != null) {
            medico.setEndereco(this.endereco.toEntity());
        }

        medico.setAtivo(true);

        // ⚠️ NÃO SETAMOS AS ROLES AQUI
        // esse passo deve ser feito no MedicoService,
        // usando roleRepository.getReferenceById()
        // exatamente como no módulo de Usuários.

        return medico;
    }

    // -----------------------------------------------------------
    // ENDEREÇO DTO
    // -----------------------------------------------------------

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

        public Endereco toEntity() {
            Endereco e = new Endereco();
            e.setLogradouro(logradouro);
            e.setNumero(numero);
            e.setComplemento(complemento);
            e.setBairro(bairro);
            e.setCidade(cidade);
            e.setUf(uf);
            e.setCep(cep);
            return e;
        }

        public static EnderecoDTO fromEntity(Endereco endereco) {
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
