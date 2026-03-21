package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.service.PacienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteControllerTest {

    @InjectMocks
    private PacienteController controller;

    @Mock
    private PacienteService service;

    @Test
    void deveListarPacientesAtivosComSucesso() {
        Pageable pageable = PageRequest.of(0, 10);

        Paciente paciente = Paciente.builder()
                .id(1L)
                .primeiroNome("João")
                .sobrenome("da Silva")
                .cpf("123.456.789-00")
                .dataNascimento(LocalDate.of(1980, 1, 1))
                .telefone("(11) 99999-0000")
                .email("joao@gmail.com")
                .sexo("M")
                .ativo(true)
                .build();

        PacienteMinDTO dto = new PacienteMinDTO(paciente);
        Page<PacienteMinDTO> page = new PageImpl<>(List.of(dto));

        when(service.listar(pageable)).thenReturn(page);

        ResponseEntity<Page<PacienteMinDTO>> response = controller.listar(pageable);
        Page<PacienteMinDTO> pacientes = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(pacientes);
        assertEquals(1, pacientes.getContent().size());
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoTiveremPacientesAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PacienteMinDTO> paginaVazia = Page.empty(pageable);

        when(service.listar(pageable)).thenReturn(paginaVazia);

        ResponseEntity<Page<PacienteMinDTO>> response = controller.listar(pageable);
        Page<PacienteMinDTO> pacientes = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(pacientes);
        assertEquals(0, pacientes.getContent().size());
    }

    @Test
    void deveLancarExcecaoQuandoServiceFalha() {
        Pageable pageable = PageRequest.of(0, 10);

        when(service.listar(pageable)).thenThrow(new RuntimeException("Erro simulado."));

        assertThrows(RuntimeException.class, () -> controller.listar(pageable));
    }
}