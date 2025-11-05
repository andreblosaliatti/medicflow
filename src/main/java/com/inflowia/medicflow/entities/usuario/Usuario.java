package com.inflowia.medicflow.entities.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import org.hibernate.validator.constraints.br.CPF;

import com.inflowia.medicflow.entities.paciente.Endereco;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "usuarios",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_usuario_login", columnNames = "login"),
            @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
            @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf")

        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false,unique = true)
    private String login;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String sobrenome;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @CPF
    @NotBlank
    @Column(nullable = false, unique = true)
    private String cpf;

    @Embedded
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    public void atualizar(String novoNome, String novoSobrenome, String novoEmail, Perfil novoPerfil,
                            Boolean  novoAtivo, Endereco novoEndereco) {
            if (novoNome != null && !novoNome.isBlank()) this.nome = novoNome;
            if (novoSobrenome != null && !novoSobrenome.isBlank()) this.sobrenome = novoSobrenome;
            if (novoEmail != null && !novoEmail.isBlank()) this.email = novoEmail;
            if (novoPerfil != null) this.perfil = novoPerfil;
            if (novoAtivo != null) this.ativo = novoAtivo;
            if (novoEndereco != null) this.endereco = novoEndereco;
                            }

}