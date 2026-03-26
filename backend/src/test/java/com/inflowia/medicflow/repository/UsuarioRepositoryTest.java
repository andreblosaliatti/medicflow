package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never"
})
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    private Usuario usuario(String login, String email, String cpf, boolean ativo, String nome, String sobrenome) {
        Usuario u = new Usuario();
        u.setLogin(login);
        u.setEmail(email);
        u.setCpf(cpf);
        u.setAtivo(ativo);
        u.setNome(nome);
        u.setSobrenome(sobrenome);
        u.setSenha("123456");
        return u;
    }

    @Test
    @DisplayName("searchForAdmin deve filtrar por nome, ativo e paginação")
    void searchForAdminShouldFilterActiveUsersAndPaginate() {
        repository.save(usuario("ana.1", "ana1@test.com", "52998224725", true, "Ana", "Silva"));
        repository.save(usuario("ana.2", "ana2@test.com", "12345678909", true, "Ana", "Souza"));
        repository.save(usuario("ana.3", "ana3@test.com", "93541134780", false, "Ana", "Costa"));
        repository.save(usuario("bruno.1", "bruno1@test.com", "71460238001", true, "Bruno", "Pereira"));

        Page<Usuario> primeiraPagina = repository.searchForAdmin("ana", true, null, PageRequest.of(0, 1));
        Page<Usuario> segundaPagina = repository.searchForAdmin("ana", true, null, PageRequest.of(1, 1));

        assertEquals(2, primeiraPagina.getTotalElements());
        assertEquals(2, primeiraPagina.getTotalPages());
        assertEquals(1, primeiraPagina.getNumberOfElements());
        assertEquals(1, segundaPagina.getNumberOfElements());
        assertTrue(primeiraPagina.getContent().stream().allMatch(Usuario::isAtivo));
        assertTrue(segundaPagina.getContent().stream().allMatch(Usuario::isAtivo));
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF")
    void shouldFindByCpf() {
        Usuario u = repository.save(usuario("joao", "joao@test.com", "86288366757", true, "João", "Silva"));

        var encontrado = repository.findByCpf("86288366757");

        assertTrue(encontrado.isPresent());
        assertEquals(u.getId(), encontrado.get().getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar CPF inexistente")
    void shouldReturnEmptyWhenCpfNotFound() {
        var encontrado = repository.findByCpf("00000000000");
        assertTrue(encontrado.isEmpty());
    }
}
