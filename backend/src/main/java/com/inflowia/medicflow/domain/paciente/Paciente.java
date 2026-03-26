package com.inflowia.medicflow.domain.paciente;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pacientes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_paciente_cpf", columnNames = "cpf")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 255)
    private String sobrenome;

    @CPF
    @Column(nullable = false, length = 14)
    private String cpf;

    @NotNull
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Formato esperado: (99) 99999-9999")
    @Column(length = 20)
    private String telefone;

    @Email
    @Column(length = 255)
    private String email;

    @NotNull
    @Column(nullable = false, length = 20)
    private String sexo;

    @Embedded
    private Endereco endereco;

    @Column(name = "plano_saude", length = 255)
    private String planoSaude;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @Builder.Default
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    public void atualizarInformacoes(PacienteUpdateDTO dados) {
        if (dados.getNome() != null) {
            this.nome = dados.getNome();
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
            if (this.endereco == null) {
                this.endereco = dados.getEndereco().toEntity();
            } else {
                this.endereco.atualizarInformacoes(dados.getEndereco());
            }
        }
        if (dados.getPlanoSaude() != null) {
            this.planoSaude = dados.getPlanoSaude();
        }
    }
}