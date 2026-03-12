package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.services.PacienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteControllerTest {

    @Mock
    PacienteService service;

    @InjectMocks
    PacienteController controller;

    @Test
    void listarRetornaPageComStatus200() {
        Page<PacienteMinDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(service.listar(PageRequest.of(0, 10))).thenReturn(page);

        var response = controller.listar(PageRequest.of(0, 10));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().getTotalElements());
    }
}
