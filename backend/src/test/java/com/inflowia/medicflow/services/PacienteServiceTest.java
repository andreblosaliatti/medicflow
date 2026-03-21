package com.inflowia.medicflow.services;

import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.repositories.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository repository;

    @InjectMocks
    private PacienteService service;

    @Test
    void deleteShouldInactivatePatient() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .ativo(true)
                .build();

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(paciente));

        service.delete(1L);

        assertFalse(paciente.isAtivo());
        verify(repository).save(paciente);
    }
}
