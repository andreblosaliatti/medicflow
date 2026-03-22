package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.domain.usuario.Medico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoMinDTO {

    private Long id;
    private String nome;
    private String sobrenome;
    private String nomeCompleto;
    private String crm;
    private String especialidade;
    private boolean ativo;

    public MedicoMinDTO(Medico entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.sobrenome = entity.getSobrenome();
        this.nomeCompleto = (entity.getNome() + " " + entity.getSobrenome()).trim();
        this.crm = entity.getCrm();
        this.especialidade = entity.getEspecialidade();
        this.ativo = entity.isAtivo();
    }
}
