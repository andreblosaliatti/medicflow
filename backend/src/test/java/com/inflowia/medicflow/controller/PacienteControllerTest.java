package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.dto.paciente.PacienteHistoricoResumoDTO;
import com.inflowia.medicflow.dto.paciente.PacienteListDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProfileDTO;
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
                .cpf("123.456.789-09")
                .dataNascimento(LocalDate.of(1980, 1, 1))
                .telefone("(11) 99999-0000")
                .email("joao@gmail.com")
                .sexo("M")
                .planoSaude("Unimed")
                .ativo(true)
                .build();

        PacienteListDTO dto = new PacienteListDTO(paciente, null);
        Page<PacienteListDTO> page = new PageImpl<>(List.of(dto));

        when(service.listar(null, null, null, null, pageable)).thenReturn(page);

        ResponseEntity<Page<PacienteListDTO>> response = controller.listar(null, null, null, null, pageable);
        Page<PacienteListDTO> pacientes = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(pacientes);
        assertEquals(1, pacientes.getContent().size());
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoTiveremPacientesAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PacienteListDTO> paginaVazia = Page.empty(pageable);

        when(service.listar(null, null, null, null, pageable)).thenReturn(paginaVazia);

        ResponseEntity<Page<PacienteListDTO>> response = controller.listar(null, null, null, null, pageable);
        Page<PacienteListDTO> pacientes = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(pacientes);
        assertEquals(0, pacientes.getContent().size());
    }

    @Test
    void deveBuscarPerfilConsolidadoDoPaciente() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .primeiroNome("João")
                .sobrenome("da Silva")
                .cpf("123.456.789-09")
                .dataNascimento(LocalDate.of(1980, 1, 1))
                .telefone("(11) 99999-0000")
                .email("joao@gmail.com")
                .sexo("M")
                .planoSaude("Unimed")
                .ativo(true)
                .build();

        PacienteProfileDTO perfil = new PacienteProfileDTO(
                paciente,
                new PacienteHistoricoResumoDTO(3, 2, 1, null, null)
        );

        when(service.buscarPerfil(1L)).thenReturn(perfil);

        ResponseEntity<PacienteProfileDTO> response = controller.buscarPerfil(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getHistorico().getTotalConsultas());
    }

    @Test
    void deveLancarExcecaoQuandoServiceFalha() {
        Pageable pageable = PageRequest.of(0, 10);

        when(service.listar(null, null, null, null, pageable)).thenThrow(new RuntimeException("Erro simulado."));

        assertThrows(RuntimeException.class, () -> controller.listar(null, null, null, null, pageable));
    }
}
