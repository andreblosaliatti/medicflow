package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.repositories.PacienteRepository;
import com.inflowia.medicflow.dto.paciente.DadosListagemPaciente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public class PacienteControllerTest {

    @InjectMocks
    private PacienteController controller;

    @Mock
    private PacienteRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
void deveListarPacientesAtivosComSucesso() {
    Pageable pageable = PageRequest.of(0, 10);
    Paciente paciente = Paciente.builder()
        .id(1L)
        .nome("João da Silva")
        .cpf("123.456.789-00")
        .dataNascimento(LocalDate.of(1980, 1, 1))
        .telefone("(11) 99999-0000")
        .email("joao@gmail.com")
        .ativo(true)
        .build();

    Page<Paciente> page = new PageImpl<>(List.of(paciente));
    when(repository.findAllByAtivoTrue(pageable)).thenReturn(page);

    ResponseEntity<Page<DadosListagemPaciente>> response = controller.listar(pageable);
    Page<DadosListagemPaciente> pacientes = response.getBody();

    assertNotNull(pacientes);
    assertEquals(1, pacientes.getContent().size());
    assertEquals("João da Silva", pacientes.getContent().get(0).nome());
}

    @Test
    void deveRetornarPaginavaziaQuandoNaoTivereamPacientesAtivos(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Paciente> paginaVazia = new PageImpl<>(List.of());

        when(repository.findAllByAtivoTrue(pageable)).thenReturn(paginaVazia);

        ResponseEntity<Page<DadosListagemPaciente>> response = controller.listar(pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Page<DadosListagemPaciente> pacientes = (Page<DadosListagemPaciente>) response.getBody();
        assertNotNull(pacientes);
        assertEquals(0, pacientes.getContent().size());
    }

    @Test
    void deveLancarExcessaoQuandoRepositorioFalha(){
        Pageable pageable = PageRequest.of(0,10);

        when(repository.findAllByAtivoTrue(pageable)).thenThrow(new RuntimeException("Erro simulado."));
        assertThrows(RuntimeException.class, () -> controller.listar(pageable));
    }

}
