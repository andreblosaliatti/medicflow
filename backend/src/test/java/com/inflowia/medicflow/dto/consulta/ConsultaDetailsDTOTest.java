package com.inflowia.medicflow.dto.consulta;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.exame.ExameBase;
import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.exame.StatusExame;
import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConsultaDetailsDTOTest {

    @Test
    void constructorMustExposeAcompanhamentoAndPrescricoesCollections() {
        Paciente paciente = new Paciente();
        paciente.setId(3L);
        paciente.setNome("Marina");
        paciente.setSobrenome("Costa");

        Medico medico = new Medico();
        medico.setId(9L);
        medico.setNome("Rafael");
        medico.setSobrenome("Lima");

        Consulta consulta = new Consulta();
        consulta.setId(12L);
        consulta.setDataHora(LocalDateTime.of(2026, 3, 21, 14, 30));
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setAnamnese("Queixa principal");
        consulta.setExameFisico("Exame físico realizado");
        consulta.setDiagnostico("Diagnóstico clínico");
        consulta.setPrescricao("Prescrição textual");
        consulta.setObservacoes("Retorno em 30 dias");

        MedicamentoBase medicamentoBase = new MedicamentoBase();
        medicamentoBase.setId(20L);

        MedicamentoPrescrito medicamento = MedicamentoPrescrito.builder()
                .id(100L)
                .nome("Dipirona")
                .dosagem("1g")
                .frequencia("8/8h")
                .via("VO")
                .medicamentoBase(medicamentoBase)
                .consulta(consulta)
                .build();

        ExameBase exameBase = new ExameBase();
        exameBase.setId(50L);
        exameBase.setNome("Hemograma");

        ExameSolicitado exame = new ExameSolicitado();
        exame.setId(200L);
        exame.setStatus(StatusExame.SOLICITADO);
        exame.setConsulta(consulta);
        exame.setExameBase(exameBase);

        consulta.setMedicamentoPrescrito(List.of(medicamento));
        consulta.setExameSolicitado(List.of(exame));

        ConsultaDetailsDTO dto = new ConsultaDetailsDTO(consulta);

        assertNotNull(dto.getAcompanhamento());
        assertEquals("Prescrição textual", dto.getAcompanhamento().getPrescricao());
        assertEquals(1, dto.getMedicamentosPrescritos().size());
        assertEquals(12L, dto.getMedicamentosPrescritos().getFirst().getConsultaId());
        assertEquals(3L, dto.getMedicamentosPrescritos().getFirst().getPacienteId());
        assertEquals(1, dto.getExamesSolicitados().size());
        assertEquals("Marina Costa", dto.getExamesSolicitados().getFirst().getPacienteNome());
    }
}
