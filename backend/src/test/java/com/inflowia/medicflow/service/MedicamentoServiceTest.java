package com.inflowia.medicflow.service;

import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicamentoBaseRepository;
import com.inflowia.medicflow.repository.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.service.validation.ConsultaDomainValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
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

    @Mock
    ConsultaDomainValidator consultaDomainValidator;

    MedicamentoService service;

    @BeforeEach
    void setUp() {
        service = new MedicamentoService(
                consultaRepository,
                medicamentoPrescritoRepository,
                medicamentoBaseRepository,
                pacienteRepository,
                consultaDomainValidator
        );
    }

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

    @Test
    void adicionarMedicamentoMustReturnBusinessRuleWhenNameAndBaseAreMissing() {
        Long consultaId = 10L;
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO();

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(new Consulta()));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.adicionarMedicamento(consultaId, dto)
        );

        assertEquals(ExceptionMessages.MEDICATION_INFO_REQUIRED, exception.getMessage());
    }

    @Test
    void adicionarMedicamentoMustRejectCanceledConsulta() {
        Long consultaId = 15L;
        Consulta consulta = new Consulta();
        consulta.setStatus(StatusConsulta.CANCELADA);

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        doThrow(new BusinessRuleException(ExceptionMessages.CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED))
                .when(consultaDomainValidator)
                .validateCanAddMedication(consulta);

        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(1L, "Dipirona", "500mg", "8/8h", "Oral");

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.adicionarMedicamento(consultaId, dto)
        );

        assertEquals(ExceptionMessages.CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED, exception.getMessage());
    }
}
