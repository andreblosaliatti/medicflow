package com.inflowia.medicflow.domain.exame;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "exame_base",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_exame_base_codigo_tuss", columnNames = "codigo_tuss")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExameBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "codigo_tuss", length = 255)
    private String codigoTuss;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoExame tipo;

    @Column(name = "prazo_estimado", nullable = false)
    private int prazoEstimado;

    @Builder.Default
    @OneToMany(mappedBy = "exameBase")
    private List<ExameSolicitado> examesSolicitados = new ArrayList<>();
}