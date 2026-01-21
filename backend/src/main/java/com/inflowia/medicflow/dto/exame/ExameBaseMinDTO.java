package com.inflowia.medicflow.dto.exame;

import com.inflowia.medicflow.entities.exame.ExameBase;
import com.inflowia.medicflow.entities.exame.TipoExame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExameBaseMinDTO {

    private Long id;
    private String nome;
    private String codigoTuss;
    private TipoExame tipo;
    private int prazoEstimado;

    public ExameBaseMinDTO(ExameBase entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.codigoTuss = entity.getCodigoTuss();
        this.tipo = entity.getTipo();
        this.prazoEstimado = entity.getPrazoEstimado();
    }
}
