package com.inflowia.medicflow.domain.consulta;

import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoConsulta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusConsulta status;

    @Column(name = "valor_consulta", precision = 10, scale = 2)
    private BigDecimal valorConsulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "meio_pagamento", nullable = false, length = 20)
    private MeioPagamento meioPagamento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pago = false;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(nullable = false)
    @Builder.Default
    private boolean retorno = false;

    @Column(name = "data_limite_retorno")
    private LocalDateTime dataLimiteRetorno;

    @Column(name = "link_acesso", length = 255)
    private String linkAcesso;

    @Column(name = "plano_saude", length = 255)
    private String planoSaude;

    @Column(name = "numero_carteirinha", length = 255)
    private String numeroCarteirinha;

    @Column(length = 500)
    private String motivo;

    @Column(length = 4000)
    private String anamnese;

    @Column(name = "exame_fisico", length = 4000)
    private String exameFisico;

    @Column(length = 4000)
    private String diagnostico;

    @Column(length = 4000)
    private String prescricao;

    @Column(length = 2000)
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Builder.Default
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicamentoPrescrito> medicamentosPrescritos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExameSolicitado> examesSolicitados = new ArrayList<>();

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
        this.pago = false;
        this.retorno = false;
    }
}