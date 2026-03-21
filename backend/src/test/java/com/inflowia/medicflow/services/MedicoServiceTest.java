package com.inflowia.medicflow.services;

import com.inflowia.medicflow.entities.usuario.Medico;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicoRepository;
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
class MedicoServiceTest {

    @Mock
    private MedicoRepository repository;

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private MedicoService service;

    @Test
    void deleteShouldInactivateDoctor() {
        Medico medico = new Medico();
        medico.setId(1L);
        medico.setAtivo(true);

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(medico));

        service.delete(1L);

        assertFalse(medico.isAtivo());
        verify(repository).save(medico);
    }
}
