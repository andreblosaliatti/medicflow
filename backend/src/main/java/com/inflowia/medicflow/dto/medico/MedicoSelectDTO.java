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
public class MedicoSelectDTO {

    private Long id;
    private String nomeCompleto;
    private String crm;
    private String especialidade;

    public MedicoSelectDTO(Medico entity) {
        this.id = entity.getId();
        this.nomeCompleto = (entity.getNome() + " " + entity.getSobrenome()).trim();
        this.crm = entity.getCrm();
        this.especialidade = entity.getEspecialidade();
    }
}
