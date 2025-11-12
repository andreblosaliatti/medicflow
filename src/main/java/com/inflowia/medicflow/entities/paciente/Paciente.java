package com.inflowia.medicflow.entities.paciente;

import com.inflowia.medicflow.entities.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import com.inflowia.medicflow.dto.paciente.DadosAtualizacaoPaciente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String primeiroNome;
    private String sobrenome;

    @Column(unique = true)
    private String cpf;

    @NotNull
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Formato esperado: (99) 99999-9999")
    private String telefone;

    @Email
    private String email;

    @NotNull
    private String sexo;

    @Embedded
    private Endereco endereco;

    private String planoSaude;

    @Builder.Default
    private boolean ativo = true;

    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    public void atualizarInformacoes(DadosAtualizacaoPaciente dados) {
        if (dados.primeiroNome() != null) this.primeiroNome = dados.primeiroNome();
        if (dados.sobrenome() != null) this.sobrenome = dados.sobrenome();
        if (dados.telefone() != null) this.telefone = dados.telefone();
        if (dados.email() != null) this.email = dados.email();
        if (dados.sexo() != null) this.sexo = dados.sexo();
        if (dados.dataNascimento() != null) this.dataNascimento = dados.dataNascimento();
        if (dados.endereco() != null) this.endereco.atualizarInformacoes(dados.endereco());
    }
}


