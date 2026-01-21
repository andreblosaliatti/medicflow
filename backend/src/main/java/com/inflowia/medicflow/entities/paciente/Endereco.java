package com.inflowia.medicflow.entities.paciente;

import com.inflowia.medicflow.dto.EnderecoDTO;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Representa o endereço de um paciente.
 */

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Endereco {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    public Endereco (String logradouro, String numero, String complemento, String bairro, String cidade, String uf, String cep){
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public void atualizarInformacoes(EnderecoDTO dados) {
        if (dados.logradouro() != null) this.logradouro = dados.logradouro();
        if (dados.numero() != null) this.numero = dados.numero();
        if (dados.complemento() != null) this.complemento = dados.complemento();
        if (dados.bairro() != null) this.bairro = dados.bairro();
        if (dados.cidade() != null) this.cidade = dados.cidade();
        if (dados.uf() != null) this.uf = dados.uf();
        if (dados.cep() != null) this.cep = dados.cep();
        }

}
