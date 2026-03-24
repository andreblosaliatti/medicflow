package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never"
})
class MedicamentoPrescritoRepositoryTest {

    @Autowired
    private MedicamentoPrescritoRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private MedicamentoBaseRepository medicamentoBaseRepository;

    @Test
    @DisplayName("findByConsultaId deve retornar somente medicamentos da consulta informada")
    void findByConsultaIdShouldReturnOnlyMedicamentosFromConsulta() {
        Paciente paciente = Paciente.builder()
                .primeiroNome("João")
                .sobrenome("Silva")
                .cpf("86288366757")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .sexo("M")
                .ativo(true)
                .build();
        paciente = pacienteRepository.save(paciente);

        Medico medico = criarMedicoValido();
        MedicamentoBase paracetamolBase = criarMedicamentoBaseValido("Paracetamol", "Paracetamol");
        MedicamentoBase ibuprofenoBase = criarMedicamentoBaseValido("Ibuprofeno", "Ibuprofeno");

        Consulta consulta1 = Consulta.builder()
                .paciente(paciente)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .teleconsulta(false)
                .dataHora(LocalDateTime.now())
                .build();
        consulta1 = consultaRepository.save(consulta1);

        Consulta consulta2 = Consulta.builder()
                .paciente(paciente)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .teleconsulta(false)
                .dataHora(LocalDateTime.now().plusDays(1))
                .build();
        consulta2 = consultaRepository.save(consulta2);

        repository.save(MedicamentoPrescrito.builder()
                .consulta(consulta1)
                .medicamentoBase(paracetamolBase)
                .nome("Paracetamol")
                .dosagem("500mg")
                .frequencia("8/8h")
                .via("VO")
                .build());

        repository.save(MedicamentoPrescrito.builder()
                .consulta(consulta2)
                .medicamentoBase(ibuprofenoBase)
                .nome("Ibuprofeno")
                .dosagem("400mg")
                .frequencia("12/12h")
                .via("VO")
                .build());

        Page<MedicamentoPrescrito> result =
                repository.findByConsultaId(consulta1.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Paracetamol", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("findByConsultaPacienteId não deve misturar medicamentos de outro paciente")
    void findByConsultaPacienteIdShouldNotMixOtherPaciente() {
        Paciente paciente1 = Paciente.builder()
                .primeiroNome("Ana")
                .sobrenome("Souza")
                .cpf("39053344705")
                .dataNascimento(LocalDate.of(1992, 2, 2))
                .sexo("F")
                .ativo(true)
                .build();
        paciente1 = pacienteRepository.save(paciente1);

        Paciente paciente2 = Paciente.builder()
                .primeiroNome("Carlos")
                .sobrenome("Oliveira")
                .cpf("12345678909")
                .dataNascimento(LocalDate.of(1988, 3, 3))
                .sexo("M")
                .ativo(true)
                .build();
        paciente2 = pacienteRepository.save(paciente2);

        Medico medico = criarMedicoValido();
        MedicamentoBase losartanaBase = criarMedicamentoBaseValido("Losartana", "Losartana");
        MedicamentoBase omeprazolBase = criarMedicamentoBaseValido("Omeprazol", "Omeprazol");

        Consulta consultaPaciente1 = Consulta.builder()
                .paciente(paciente1)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .teleconsulta(false)
                .dataHora(LocalDateTime.now())
                .build();
        consultaPaciente1 = consultaRepository.save(consultaPaciente1);

        Consulta consultaPaciente2 = Consulta.builder()
                .paciente(paciente2)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .teleconsulta(false)
                .dataHora(LocalDateTime.now().plusDays(1))
                .build();
        consultaPaciente2 = consultaRepository.save(consultaPaciente2);

        repository.save(MedicamentoPrescrito.builder()
                .consulta(consultaPaciente1)
                .medicamentoBase(losartanaBase)
                .nome("Losartana")
                .dosagem("50mg")
                .frequencia("1x ao dia")
                .via("VO")
                .build());

        repository.save(MedicamentoPrescrito.builder()
                .consulta(consultaPaciente2)
                .medicamentoBase(omeprazolBase)
                .nome("Omeprazol")
                .dosagem("20mg")
                .frequencia("1x ao dia")
                .via("VO")
                .build());

        Page<MedicamentoPrescrito> result =
                repository.findByConsultaPacienteId(paciente1.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Losartana", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("findByConsultaId não deve trazer medicamentos de outra consulta do mesmo paciente")
    void findByConsultaIdShouldNotBringMedicamentosFromAnotherConsultaOfSamePaciente() {
        Paciente paciente = Paciente.builder()
                .primeiroNome("Marina")
                .sobrenome("Costa")
                .cpf("29537988001")
                .dataNascimento(LocalDate.of(1995, 4, 4))
                .sexo("F")
                .ativo(true)
                .build();
        paciente = pacienteRepository.save(paciente);

        Medico medico = criarMedicoValido();
        MedicamentoBase dipironaBase = criarMedicamentoBaseValido("Dipirona", "Dipirona");
        MedicamentoBase amoxicilinaBase = criarMedicamentoBaseValido("Amoxicilina", "Amoxicilina");

        Consulta consulta1 = Consulta.builder()
                .paciente(paciente)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .teleconsulta(false)
                .dataHora(LocalDateTime.now())
                .build();
        consulta1 = consultaRepository.save(consulta1);

        Consulta consulta2 = Consulta.builder()
                .paciente(paciente)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .teleconsulta(false)
                .dataHora(LocalDateTime.now().plusDays(2))
                .build();
        consulta2 = consultaRepository.save(consulta2);

        repository.save(MedicamentoPrescrito.builder()
                .consulta(consulta1)
                .medicamentoBase(dipironaBase)
                .nome("Dipirona")
                .dosagem("1g")
                .frequencia("6/6h")
                .via("VO")
                .build());

        repository.save(MedicamentoPrescrito.builder()
                .consulta(consulta2)
                .medicamentoBase(amoxicilinaBase)
                .nome("Amoxicilina")
                .dosagem("500mg")
                .frequencia("8/8h")
                .via("VO")
                .build());

        Page<MedicamentoPrescrito> result =
                repository.findByConsultaId(consulta1.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(m -> "Dipirona".equals(m.getNome())));
    }

    private Medico criarMedicoValido() {
        Medico medico = new Medico();
        medico.setLogin("medico" + System.nanoTime());
        medico.setSenha("123456");
        medico.setNome("João");
        medico.setSobrenome("Silva");
        medico.setEmail("medico" + System.nanoTime() + "@test.com");
        medico.setCpf(gerarCpfUnicoValido());
        medico.setAtivo(true);
        medico.setCrm("CRM" + System.nanoTime());
        medico.setEspecialidade("Clínico Geral");
        medico.setSexo("M");

        return medicoRepository.save(medico);
    }

    private MedicamentoBase criarMedicamentoBaseValido(String nomeComercial, String principioAtivo) {
        return medicamentoBaseRepository.save(
                MedicamentoBase.builder()
                        .nomeComercial(nomeComercial)
                        .principioAtivo(principioAtivo)
                        .ativo(true)
                        .controlado(false)
                        .build()
        );
    }

    private String gerarCpfUnicoValido() {
        return "52998224725";
    }
}