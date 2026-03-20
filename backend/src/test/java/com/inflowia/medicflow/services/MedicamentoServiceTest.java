package com.inflowia.medicflow.services;

import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicamentoBaseRespository;
import com.inflowia.medicflow.repositories.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repositories.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    ConsultaRepository consultaRepository;
    @Mock
    MedicamentoPrescritoRepository medicamentoRepository;
    @Mock
    MedicamentoBaseRespository medicamentoBaseRespository;
    @Mock
    PacienteRepository pacienteRepository;

    @InjectMocks
    MedicamentoService service;

    @Test
    void listarMedicamentosMustQueryByConsultaId() {
        Long consultaId = 10L;
        var pageable = PageRequest.of(0, 10);
        when(consultaRepository.existsById(consultaId)).thenReturn(true);
        when(medicamentoRepository.findByConsultaId(consultaId, pageable)).thenReturn(new PageImpl<>(java.util.List.of()));

        service.listarMedicamentos(consultaId, pageable);

        verify(medicamentoRepository).findByConsultaId(consultaId, pageable);
    }
}
