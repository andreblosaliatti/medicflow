package com.inflowia.medicflow.entities.paciente;

import com.inflowia.medicflow.entities.consulta.Consulta;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
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

    public void atualizarInformacoes(PacienteUpdateDTO dados) {
        if (dados.getPrimeiroNome() != null) {
            this.primeiroNome = dados.getPrimeiroNome();
        }
        if (dados.getSobrenome() != null) {
            this.sobrenome = dados.getSobrenome();
        }
        if (dados.getTelefone() != null) {
            this.telefone = dados.getTelefone();
        }
        if (dados.getEmail() != null) {
            this.email = dados.getEmail();
        }
        if (dados.getSexo() != null) {
            this.sexo = dados.getSexo();
        }
        if (dados.getDataNascimento() != null) {
            this.dataNascimento = dados.getDataNascimento();
        }
        if (dados.getEndereco() != null) {
            this.endereco.atualizarInformacoes(dados.getEndereco());
        }
    }
}


