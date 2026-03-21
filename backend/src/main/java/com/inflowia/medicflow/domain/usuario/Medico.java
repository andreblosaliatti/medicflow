package com.inflowia.medicflow.domain.usuario;

import com.inflowia.medicflow.domain.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "usuario_id")

public class Medico extends Usuario  {

    @Column(nullable = false, unique = true)
    private String crm;
    private String especialidade;
    private String instituicaoFormacao;
    private LocalDate dataFormacao;
    private String sexo;
    private String observacoes;

    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultasMedico = new ArrayList<>();

}
