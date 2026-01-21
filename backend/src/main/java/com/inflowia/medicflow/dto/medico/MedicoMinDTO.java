package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.entities.usuario.Medico;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoMinDTO {

    private Long id;
    private String nome;
    private String sobrenome;
    private String crm;
    private String especialidade;
    private boolean ativo;

    public MedicoMinDTO(Medico entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.sobrenome = entity.getSobrenome();
        this.crm = entity.getCrm();
        this.especialidade = entity.getEspecialidade();
        this.ativo = entity.isAtivo();
    }
}
