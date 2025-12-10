package com.inflowia.medicflow.entities.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import org.hibernate.validator.constraints.br.CPF;

import com.inflowia.medicflow.entities.paciente.Endereco;

import java.util.HashSet;
import java.util.Set;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "tb_usuarios",
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


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_role",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;
}