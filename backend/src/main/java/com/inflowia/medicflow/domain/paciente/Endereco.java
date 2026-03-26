package com.inflowia.medicflow.domain.paciente;

import com.inflowia.medicflow.dto.EnderecoDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Column(length = 255)
    private String logradouro;

    @Column(length = 50)
    private String numero;

    @Column(length = 255)
    private String complemento;

    @Column(length = 255)
    private String bairro;

    @Column(length = 255)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 20)
    private String cep;

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