package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.exame.ExameBase;
import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.exame.StatusExame;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoDetailsDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoPatchDTO;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.ExameBaseRepository;
import com.inflowia.medicflow.repository.ExameSolicitadoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ExameSolicitadoServiceTest {

    @Mock
    private ExameSolicitadoRepository exameSolicitadoRepository;

    @Mock
    private ExameBaseRepository exameBaseRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    private ExameSolicitadoService service;

    @BeforeEach
    void setUp() {
        service = new ExameSolicitadoService();
        service.getClass();
    }

    @Test
    void buscarPorIdMustMapPacienteSobrenomeCorretamente() throws Exception {
        injectDependencies();
        ExameSolicitado entity = exameSolicitadoComPaciente();

        when(exameSolicitadoRepository.findById(1L)).thenReturn(Optional.of(entity));

        ExameSolicitadoDetailsDTO result = service.buscarPorId(1L);

        assertEquals("Ana", result.getPacientePrimeiroNome());
        assertEquals("Silva", result.getPacienteSobrenome());
    }

    @Test
    void atualizarMustReplaceCamposPermitidos() throws Exception {
        injectDependencies();
        ExameSolicitado entity = exameSolicitadoComPaciente();
        ExameSolicitadoPatchDTO dto = new ExameSolicitadoPatchDTO(
                StatusExame.CANCELADO,
                null,
                null,
                "Cancelado por orientação clínica"
        );

        when(exameSolicitadoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(exameSolicitadoRepository.save(entity)).thenReturn(entity);

        ExameSolicitadoDetailsDTO result = service.atualizar(1L, dto);

        assertEquals(StatusExame.CANCELADO, result.getStatus());
        assertNull(result.getDataColeta());
        assertNull(result.getDataResultado());
        assertEquals("Cancelado por orientação clínica", result.getObservacoes());
    }

    @Test
    void atualizarParcialmenteMustPreserveCamposNaoInformados() throws Exception {
        injectDependencies();
        ExameSolicitado entity = exameSolicitadoComPaciente();
        LocalDateTime dataResultado = LocalDateTime.of(2026, 3, 21, 10, 15);
        ExameSolicitadoPatchDTO dto = new ExameSolicitadoPatchDTO(null, null, dataResultado, null);

        when(exameSolicitadoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(exameSolicitadoRepository.save(entity)).thenReturn(entity);

        ExameSolicitadoDetailsDTO result = service.atualizarParcialmente(1L, dto);

        assertEquals(StatusExame.AGENDADO, result.getStatus());
        assertEquals(LocalDateTime.of(2026, 3, 20, 9, 0), result.getDataColeta());
        assertEquals(dataResultado, result.getDataResultado());
        assertEquals("Jejum de 8h", result.getObservacoes());
    }

    @Test
    void listarPorPacienteMustQueryRepositoryWithPacienteId() throws Exception {
        injectDependencies();
        Long pacienteId = 7L;
        var pageable = PageRequest.of(0, 10);

        when(pacienteRepository.existsByIdAndAtivoTrue(pacienteId)).thenReturn(true);
        when(exameSolicitadoRepository.findByConsultaPacienteId(pacienteId, pageable))
                .thenReturn(new PageImpl<>(List.of(exameSolicitadoComPaciente())));

        service.listarPorPaciente(pacienteId, pageable);

        verify(exameSolicitadoRepository).findByConsultaPacienteId(pacienteId, pageable);
    }

    @Test
    void atualizarParcialmenteMustRejectInvalidStatusTransition() throws Exception {
        injectDependencies();
        ExameSolicitado entity = exameSolicitadoComPaciente();
        entity.setStatus(StatusExame.CANCELADO);
        ExameSolicitadoPatchDTO dto = new ExameSolicitadoPatchDTO(StatusExame.AGENDADO, null, null, null);

        when(exameSolicitadoRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(
                com.inflowia.medicflow.exception.BusinessRuleException.class,
                () -> service.atualizarParcialmente(1L, dto)
        );
    }

    private void injectDependencies() throws Exception {
        inject("exameSolicitadoRepository", exameSolicitadoRepository);
        inject("exameBaseRepository", exameBaseRepository);
        inject("consultaRepository", consultaRepository);
        inject("pacienteRepository", pacienteRepository);
    }

    private void inject(String fieldName, Object value) throws Exception {
        var field = ExameSolicitadoService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }

    private ExameSolicitado exameSolicitadoComPaciente() {
        Paciente paciente = new Paciente();
        paciente.setId(7L);
        paciente.setNome("Ana");
        paciente.setSobrenome("Silva");

        Consulta consulta = new Consulta();
        consulta.setId(5L);
        consulta.setPaciente(paciente);

        ExameBase exameBase = new ExameBase();
        exameBase.setId(3L);
        exameBase.setNome("Hemograma");
        exameBase.setCodigoTuss("40304361");

        ExameSolicitado entity = new ExameSolicitado();
        entity.setId(1L);
        entity.setStatus(StatusExame.AGENDADO);
        entity.setConsulta(consulta);
        entity.setExameBase(exameBase);
        entity.setObservacoes("Jejum de 8h");
        entity.setDataColeta(LocalDateTime.of(2026, 3, 20, 9, 0));
        return entity;
    }
}
