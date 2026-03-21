package com.inflowia.medicflow.services;

import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicamentoBaseRepository;
import com.inflowia.medicflow.repositories.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repositories.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    ConsultaRepository consultaRepository;

    @Mock
    MedicamentoPrescritoRepository medicamentoPrescritoRepository;

    @Mock
    MedicamentoBaseRepository medicamentoBaseRepository;

    @Mock
    PacienteRepository pacienteRepository;

    @InjectMocks
    MedicamentoService service;

    @Test
    void listarPorConsultaMustQueryByConsultaId() {
        Long consultaId = 10L;
        var pageable = PageRequest.of(0, 10);

        when(consultaRepository.existsById(consultaId)).thenReturn(true);
        when(medicamentoPrescritoRepository.findByConsultaId(consultaId, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        service.listarPorConsulta(consultaId, pageable);

        verify(medicamentoPrescritoRepository).findByConsultaId(consultaId, pageable);
    }
}