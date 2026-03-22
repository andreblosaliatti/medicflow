package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    @DisplayName("findByCpfAndAtivoTrue deve ignorar usuários inativos")
    void findByCpfAndAtivoTrueShouldIgnoreInactiveUsers() {
        Usuario ativo = repository.save(usuario("usuario.ativo", "ativo@test.com", "32844208606", true));
        repository.save(usuario("usuario.inativo", "inativo@test.com", "39053344705", false));

        Optional<Usuario> encontrado = repository.findByCpfAndAtivoTrue(ativo.getCpf());
        Optional<Usuario> naoEncontrado = repository.findByCpfAndAtivoTrue("39053344705");

        assertTrue(encontrado.isPresent());
        assertEquals("usuario.ativo", encontrado.get().getLogin());
        assertTrue(naoEncontrado.isEmpty());
    }

    @Test
    @DisplayName("findByNomeContainingIgnoreCaseAndAtivoTrue deve respeitar filtro por ativo e paginação")
    void findByNomeContainingIgnoreCaseAndAtivoTrueShouldFilterActiveUsersAndPaginate() {
        repository.save(usuario("ana.1", "ana1@test.com", "11144477735", true, "Ana", "Silva"));
        repository.save(usuario("ana.2", "ana2@test.com", "22255588846", true, "Ana", "Souza"));
        repository.save(usuario("ana.3", "ana3@test.com", "33366699957", false, "Ana", "Costa"));
        repository.save(usuario("bruno.1", "bruno1@test.com", "44477700083", true, "Bruno", "Pereira"));

        Page<Usuario> primeiraPagina = repository.findByNomeContainingIgnoreCaseAndAtivoTrue("ana", PageRequest.of(0, 1));
        Page<Usuario> segundaPagina = repository.findByNomeContainingIgnoreCaseAndAtivoTrue("ana", PageRequest.of(1, 1));

        assertEquals(2, primeiraPagina.getTotalElements());
        assertEquals(2, primeiraPagina.getTotalPages());
        assertEquals(1, primeiraPagina.getNumberOfElements());
        assertEquals(1, segundaPagina.getNumberOfElements());
        assertTrue(primeiraPagina.getContent().stream().allMatch(Usuario::isAtivo));
        assertTrue(segundaPagina.getContent().stream().allMatch(Usuario::isAtivo));
    }

    private Usuario usuario(String login, String email, String cpf, boolean ativo) {
        return usuario(login, email, cpf, ativo, "Usuario", "Teste");
    }

    private Usuario usuario(String login, String email, String cpf, boolean ativo, String nome, String sobrenome) {
        return Usuario.builder()
                .login(login)
                .senha("encoded")
                .nome(nome)
                .sobrenome(sobrenome)
                .email(email)
                .cpf(cpf)
                .ativo(ativo)
                .build();
    }
}
