package com.inflowia.medicflow.entities.paciente;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import com.inflowia.medicflow.dto.paciente.DadosAtualizacaoPaciente;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa os dados de um paciente no sistema MedicFlow.
 */
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

    @NotBlank
    private String nome;

    @CPF
    @Column(unique = true)
    private String cpf;

    @NotNull
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Formato esperado: (99) 99999-9999")
    private String telefone;

    @Email
    private String email;

    @Embedded
    private Endereco endereco;

    private String planoSaude;

    @Builder.Default
    private boolean ativo = true;

    public void atualizarInformacoes(DadosAtualizacaoPaciente dados){
    if (dados.nome() != null) this.nome = dados.nome();
    if (dados.telefone() != null) this.telefone = dados.telefone();
    if (dados.email() != null) this.email = dados.email();
    if (dados.dataNascimento() != null) this.dataNascimento = dados.dataNascimento();
    if (dados.endereco() != null) this.endereco.atualizarInformacoes(dados.endereco());
}

@Builder.Default
@OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicamentoPrescrito> medicamentosAtuais = new ArrayList<>();

}


