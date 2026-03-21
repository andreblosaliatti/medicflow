package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.dto.consulta.ConsultaDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDetailsDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaFilterDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaUpdateDTO;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.service.validation.ConsultaDomainValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    ConsultaRepository consultaRepository;

    @Mock
    PacienteRepository pacienteRepository;

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    ConsultaDomainValidator consultaDomainValidator;

    @InjectMocks
    ConsultaService service;

    @Test
    void criarMustValidateTeleconsultaBeforeSaving() {
        ConsultaDTO dto = new ConsultaDTO(
                LocalDateTime.now().plusDays(1),
                TipoConsulta.TELECONSULTA,
                StatusConsulta.AGENDADA,
                150.0,
                MeioPagamento.PIX,
                false,
                null,
                30,
                false,
                null,
                true,
                null,
                null,
                null,
                "Revisão clínica",
                null,
                null,
                null,
                null,
                null,
                1L,
                2L
        );

        when(pacienteRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(new Paciente()));
        when(medicoRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(new Medico()));
        org.mockito.Mockito.doThrow(new BusinessRuleException(ExceptionMessages.TELECONSULTATION_LINK_REQUIRED))
                .when(consultaDomainValidator)
                .validate(any(Consulta.class));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> service.criar(dto));

        assertEquals(ExceptionMessages.TELECONSULTATION_LINK_REQUIRED, exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void atualizarMustRejectPrescriptionOnCanceledConsulta() {
        Long consultaId = 20L;
        Consulta consulta = consultaBase(StatusConsulta.CONFIRMADA);
        consulta.setId(consultaId);

        ConsultaUpdateDTO dto = ConsultaUpdateDTO.builder()
                .status(StatusConsulta.CANCELADA)
                .prescricao("Prescrever antibiótico")
                .build();

        when(consultaRepository.getReferenceById(consultaId)).thenReturn(consulta);
        org.mockito.Mockito.doThrow(new BusinessRuleException(ExceptionMessages.CANCELED_CONSULTATION_PRESCRIPTION_NOT_ALLOWED))
                .when(consultaDomainValidator)
                .validate(any(Consulta.class));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> service.atualizar(consultaId, dto));

        assertEquals(ExceptionMessages.CANCELED_CONSULTATION_PRESCRIPTION_NOT_ALLOWED, exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
        verify(consultaDomainValidator).validateStatusTransition(StatusConsulta.CONFIRMADA, StatusConsulta.CANCELADA);
    }

    @Test
    void confirmarMustUpdateStatusToConfirmada() {
        Consulta consulta = consultaBase(StatusConsulta.AGENDADA);
        consulta.setId(10L);
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        ConsultaDetailsDTO result = service.confirmar(10L);

        assertEquals(StatusConsulta.CONFIRMADA, result.getStatus());
        verify(consultaDomainValidator).validateCanConfirm(consulta);
        verify(consultaDomainValidator).validateStatusTransition(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA);
    }

    @Test
    void listarMustApplySpecificationBackedSearch() {
        Consulta consulta = consultaBase(StatusConsulta.CONFIRMADA);
        consulta.setId(33L);
        when(consultaRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(consulta), PageRequest.of(0, 20), 1));

        var page = service.listar(ConsultaFilterDTO.builder().status(StatusConsulta.CONFIRMADA).termo("avaliacao").build(), PageRequest.of(0, 20));

        assertEquals(1, page.getTotalElements());
        assertEquals(33L, page.getContent().getFirst().getId());
    }

    private Consulta consultaBase(StatusConsulta status) {
        Consulta consulta = new Consulta();
        consulta.setStatus(status);
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPaciente(new Paciente(1L, "Ana", "Souza", null, null, null, null, null, null, null, true, List.of()));
        Medico medico = new Medico();
        medico.setId(2L);
        medico.setNome("Carlos");
        medico.setSobrenome("Silva");
        consulta.setMedico(medico);
        consulta.setMotivo("Avaliação clínica");
        return consulta;
    }
}
