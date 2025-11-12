package com.inflowia.medicflow.entities.consulta;

import com.inflowia.medicflow.entities.exame.ExameSolicitado;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConsulta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsulta status;

    @Column(length = 500)
    private String motivo;

    @Column(length = 4000)
    private String anamnese;

    @Column(length = 4000)
    private String exameFisico;

    @Column(length = 4000)
    private String diagnostico;

    @Column(length = 4000)
    private String prescricao;

    @Column(length = 2000)
    private String observacoes;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @OneToMany(mappedBy = "consulta")
    private List<MedicamentoPrescrito> medicamentoPrescrito = new ArrayList<>();

    @OneToMany(mappedBy = "consulta")
    private List<ExameSolicitado> exameSolicitado = new ArrayList<>();

    public Consulta(LocalDateTime dataHora,
                    TipoConsulta tipo,
                    StatusConsulta status,
                    String motivo,
                    Paciente paciente,
                    Medico medico) {
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.status = status;
        this.motivo = motivo;
        this.paciente = paciente;
        this.medico = medico;
    }
}
