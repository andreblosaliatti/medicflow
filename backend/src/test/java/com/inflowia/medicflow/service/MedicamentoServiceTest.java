package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicamentoBaseRepository;
import com.inflowia.medicflow.repository.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.service.validation.ConsultaDomainValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(null, null, "500mg", "8/8h", "Oral");

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consultaValida()));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.adicionarMedicamento(consultaId, dto)
        );

        assertEquals(ExceptionMessages.MEDICATION_INFO_REQUIRED, exception.getMessage());
    }

    @Test
    void adicionarMedicamentoMustRejectWhenBaseAndFreeNameAreProvidedTogether() {
        Long consultaId = 10L;
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(1L, "Dipirona", "500mg", "8/8h", "Oral");

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consultaValida()));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.adicionarMedicamento(consultaId, dto)
        );

        assertEquals(ExceptionMessages.MEDICATION_INFO_CONFLICT, exception.getMessage());
    }

    @Test
    void adicionarMedicamentoMustAllowFreeTextMedicationWithoutBase() {
        Long consultaId = 10L;
        Consulta consulta = consultaValida();
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(null, "Fórmula manipulada", "10mg", "1x/dia", "Oral");

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(medicamentoPrescritoRepository.save(any(MedicamentoPrescrito.class)))
                .thenAnswer(invocation -> {
                    MedicamentoPrescrito salvo = invocation.getArgument(0);
                    salvo.setId(77L);
                    return salvo;
                });

        var result = service.adicionarMedicamento(consultaId, dto);

        assertEquals(77L, result.getId());
        assertEquals("Fórmula manipulada", result.getNome());
        assertNull(consulta.getMedicamentoPrescrito().get(0).getMedicamentoBase());
    }

    @Test
    void adicionarMedicamentoMustUseBaseDataWhenBaseMedicationIsProvided() {
        Long consultaId = 10L;
        Consulta consulta = consultaValida();
        MedicamentoBase base = MedicamentoBase.builder()
                .id(3L)
                .nomeComercial("Amoxicilina 500mg")
                .principioAtivo("Amoxicilina")
                .build();
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(3L, null, "500mg", "8/8h", "Oral");

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(medicamentoBaseRepository.findById(3L)).thenReturn(Optional.of(base));
        when(medicamentoPrescritoRepository.save(any(MedicamentoPrescrito.class)))
                .thenAnswer(invocation -> {
                    MedicamentoPrescrito salvo = invocation.getArgument(0);
                    salvo.setId(88L);
                    return salvo;
                });

        var result = service.adicionarMedicamento(consultaId, dto);

        assertEquals("Amoxicilina", result.getNome());
        assertEquals(base, consulta.getMedicamentoPrescrito().get(0).getMedicamentoBase());
    }

    @Test
    void adicionarMedicamentoMustFallbackToDcbWhenBaseHasNoPrincipioAtivoOrNomeComercial() {
        Long consultaId = 10L;
        Consulta consulta = consultaValida();
        MedicamentoBase base = MedicamentoBase.builder()
                .id(9L)
                .dcb("Losartana")
                .build();
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(9L, null, "50mg", "1x/dia", "Oral");

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(medicamentoBaseRepository.findById(9L)).thenReturn(Optional.of(base));
        when(medicamentoPrescritoRepository.save(any(MedicamentoPrescrito.class)))
                .thenAnswer(invocation -> {
                    MedicamentoPrescrito salvo = invocation.getArgument(0);
                    salvo.setId(99L);
                    return salvo;
                });

        var result = service.adicionarMedicamento(consultaId, dto);

        assertEquals(99L, result.getId());
        assertEquals("Losartana", result.getNome());
        assertEquals(base, consulta.getMedicamentoPrescrito().get(0).getMedicamentoBase());
    }

    @Test
    void atualizarMedicamentoMustSwitchToFreeTextWhenClinicalFlowRequiresEdit() {
        Consulta consulta = consultaValida();
        MedicamentoBase base = MedicamentoBase.builder()
                .id(4L)
                .nomeComercial("Paracetamol 750mg")
                .principioAtivo("Paracetamol")
                .build();
        MedicamentoPrescrito prescrito = MedicamentoPrescrito.builder()
                .id(25L)
                .consulta(consulta)
                .medicamentoBase(base)
                .nome("Paracetamol")
                .dosagem("750mg")
                .frequencia("8/8h")
                .via("Oral")
                .build();
        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(null, "Composto individualizado", "12mg", "2x/dia", "Sublingual");

        when(medicamentoPrescritoRepository.findById(25L)).thenReturn(Optional.of(prescrito));
        when(medicamentoPrescritoRepository.save(prescrito)).thenReturn(prescrito);

        var result = service.atualizarMedicamento(25L, dto);

        assertEquals("Composto individualizado", result.getNome());
        assertNull(prescrito.getMedicamentoBase());
        assertEquals("12mg", prescrito.getDosagem());
        assertEquals("2x/dia", prescrito.getFrequencia());
        assertEquals("Sublingual", prescrito.getVia());
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

        MedicamentoPrescritoDTO dto = new MedicamentoPrescritoDTO(1L, null, "500mg", "8/8h", "Oral");

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> service.adicionarMedicamento(consultaId, dto)
        );

        assertEquals(ExceptionMessages.CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED, exception.getMessage());
    }

    private Consulta consultaValida() {
        Consulta consulta = new Consulta();
        consulta.setMedicamentoPrescrito(new ArrayList<>());
        consulta.setStatus(StatusConsulta.CONCLUIDA);
        return consulta;
    }
}
