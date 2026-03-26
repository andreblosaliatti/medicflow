package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteListDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProfileDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.ExameSolicitadoRepository;
import com.inflowia.medicflow.repository.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository repository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ExameSolicitadoRepository exameSolicitadoRepository;

    @Mock
    private MedicamentoPrescritoRepository medicamentoPrescritoRepository;

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

    @Test
    void listarShouldApplyFiltersAndReturnTableDto() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .nome("Maria")
                .sobrenome("Oliveira")
                .cpf("29537988001")
                .telefone("(11) 99999-0000")
                .planoSaude("Unimed")
                .ativo(true)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Paciente> page = new PageImpl<>(List.of(paciente), pageable, 1);
        LocalDateTime ultimaConsulta = LocalDateTime.of(2026, 3, 1, 10, 0);

        when(repository.search("Maria", "123", true, "Uni", pageable)).thenReturn(page);
        when(consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(1L))
                .thenReturn(Optional.of(Consulta.builder().id(99L).dataHora(ultimaConsulta).build()));

        Page<PacienteListDTO> resultado = service.listar(" Maria ", "123", null, "Uni", pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Maria Oliveira", resultado.getContent().getFirst().getNomeCompleto());
        assertEquals(ultimaConsulta, resultado.getContent().getFirst().getUltimaConsulta());
        verify(repository).search("Maria", "123", true, "Uni", pageable);
    }

    @Test
    void buscarPerfilShouldReturnConsolidatedSummary() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .nome("João")
                .sobrenome("da Silva")
                .cpf("38475612040")
                .dataNascimento(LocalDate.of(1980, 1, 1))
                .telefone("(11) 99999-0000")
                .email("joao@gmail.com")
                .sexo("M")
                .planoSaude("Unimed")
                .ativo(true)
                .build();

        Medico medico = new Medico();
        medico.setNome("Ana");
        medico.setSobrenome("Souza");

        Consulta ultimaConsulta = Consulta.builder()
                .id(10L)
                .dataHora(LocalDateTime.of(2026, 3, 10, 14, 30))
                .tipo(TipoConsulta.RETORNO)
                .status(StatusConsulta.CONCLUIDA)
                .motivo("Acompanhamento")
                .medico(medico)
                .build();

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(paciente));
        when(consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(1L)).thenReturn(Optional.of(ultimaConsulta));
        when(consultaRepository.countByPacienteId(1L)).thenReturn(5L);
        when(exameSolicitadoRepository.countByConsultaPacienteId(1L)).thenReturn(3L);
        when(medicamentoPrescritoRepository.countByConsultaPacienteId(1L)).thenReturn(2L);

        PacienteProfileDTO perfil = service.buscarPerfil(1L);

        assertEquals("João da Silva", perfil.getNomeCompleto());
        assertEquals("Unimed", perfil.getPlanoSaude());
        assertEquals(5L, perfil.getHistorico().getTotalConsultas());
        assertEquals(3L, perfil.getHistorico().getTotalExamesSolicitados());
        assertEquals(2L, perfil.getHistorico().getTotalMedicamentosPrescritos());
        assertEquals("Ana Souza", perfil.getHistorico().getUltimaConsulta().getMedicoNome());
    }

    @Test
    void atualizarShouldCopyPlanoSaudeAndBirthDate() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .nome("João")
                .sobrenome("da Silva")
                .planoSaude("Antigo")
                .dataNascimento(LocalDate.of(1980, 1, 1))
                .ativo(true)
                .build();

        PacienteUpdateDTO dto = new PacienteUpdateDTO(
                null,
                null,
                null,
                null,
                null,
                LocalDate.of(1981, 2, 2),
                null,
                "Novo Convênio"
        );

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(paciente));
        when(repository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PacienteDTO atualizado = service.atualizar(1L, dto);

        assertEquals(LocalDate.of(1981, 2, 2), atualizado.getDataNascimento());
        assertEquals("Novo Convênio", atualizado.getPlanoSaude());
    }

    @Test
    void buscarPerfilShouldHandlePacienteSemConsultas() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .nome("João")
                .sobrenome("da Silva")
                .ativo(true)
                .build();

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(paciente));
        when(consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(1L)).thenReturn(Optional.empty());
        when(consultaRepository.countByPacienteId(1L)).thenReturn(0L);
        when(exameSolicitadoRepository.countByConsultaPacienteId(1L)).thenReturn(0L);
        when(medicamentoPrescritoRepository.countByConsultaPacienteId(1L)).thenReturn(0L);

        PacienteProfileDTO perfil = service.buscarPerfil(1L);

        assertNull(perfil.getHistorico().getDataUltimaConsulta());
        assertNull(perfil.getHistorico().getUltimaConsulta());
        assertTrue(perfil.isAtivo());
    }
}
