package com.inflowia.medicflow.repositories;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.MeioPagamento;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(useDefaultFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
@ContextConfiguration(classes = MedicamentoPrescritoRepositoryTest.JpaTestConfig.class)
class MedicamentoPrescritoRepositoryTest {

    @Configuration
    @EnableJpaRepositories(basePackageClasses = MedicamentoPrescritoRepository.class)
    @EntityScan(basePackages = "com.inflowia.medicflow.entities")
    static class JpaTestConfig {
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MedicamentoPrescritoRepository repository;

    @Test
    @DisplayName("Deve retornar somente medicamentos da consulta informada")
    void findAllByConsultaIdShouldReturnOnlyMedicamentosFromConsulta() {
        Paciente paciente = persistPaciente("Paciente A", "11111111111");
        Medico medico = persistMedico("medico1");

        Consulta consultaA = persistConsulta(paciente, medico, LocalDateTime.now().minusDays(1));
        Consulta consultaB = persistConsulta(paciente, medico, LocalDateTime.now());

        MedicamentoPrescrito medA = persistMedicamento("Dipirona", consultaA);
        persistMedicamento("Ibuprofeno", consultaB);

        entityManager.flush();
        entityManager.clear();

        Page<MedicamentoPrescrito> page =
                repository.findAllByConsultaId(consultaA.getId(), PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(medA.getId());
        assertThat(page.getContent().get(0).getNome()).isEqualTo("Dipirona");
    }

    @Test
    @DisplayName("Não deve misturar medicamentos de outro paciente")
    void findAllByConsultaPacienteIdShouldNotMixOtherPaciente() {
        Paciente pacienteA = persistPaciente("Paciente A", "22222222222");
        Paciente pacienteB = persistPaciente("Paciente B", "33333333333");
        Medico medico = persistMedico("medico2");

        Consulta consultaA = persistConsulta(pacienteA, medico, LocalDateTime.now().minusDays(1));
        Consulta consultaB = persistConsulta(pacienteB, medico, LocalDateTime.now());

        MedicamentoPrescrito medA = persistMedicamento("Losartana", consultaA);
        persistMedicamento("Amoxicilina", consultaB);

        entityManager.flush();
        entityManager.clear();

        Page<MedicamentoPrescrito> page =
                repository.findAllByConsultaPacienteId(pacienteA.getId(), PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(medA.getId());
        assertThat(page.getContent().get(0).getNome()).isEqualTo("Losartana");
    }

    @Test
    @DisplayName("Não deve trazer medicamentos de outra consulta do mesmo paciente")
    void findAllByConsultaIdShouldNotBringMedicamentosFromAnotherConsultaOfSamePaciente() {
        Paciente paciente = persistPaciente("Paciente A", "44444444444");
        Medico medico = persistMedico("medico3");

        Consulta consulta1 = persistConsulta(paciente, medico, LocalDateTime.now().minusDays(2));
        Consulta consulta2 = persistConsulta(paciente, medico, LocalDateTime.now().minusDays(1));

        MedicamentoPrescrito med1 = persistMedicamento("Omeprazol", consulta1);
        persistMedicamento("Metformina", consulta2);

        entityManager.flush();
        entityManager.clear();

        Page<MedicamentoPrescrito> page =
                repository.findAllByConsultaId(consulta1.getId(), PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(med1.getId());
        assertThat(page.getContent().get(0).getNome()).isEqualTo("Omeprazol");
    }

    private Paciente persistPaciente(String nome, String cpf) {
        Paciente paciente = Paciente.builder()
                .primeiroNome(nome)
                .sobrenome("Teste")
                .cpf(cpf)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .telefone("(51) 99999-9999")
                .email(cpf + "@teste.com")
                .sexo("M")
                .ativo(true)
                .build();

        entityManager.persist(paciente);
        return paciente;
    }

    private Medico persistMedico(String sufixo) {
        Medico medico = new Medico();

        medico.setNome("Joao");
        medico.setSobrenome("Silva " + sufixo);
        medico.setCpf(cpfValidoPorSufixo(sufixo));
        medico.setEmail("medico" + sufixo + "@teste.com");
        medico.setLogin("medico_" + sufixo);
        medico.setSenha("123456");
        medico.setSexo("M");

        medico.setCrm("CRM-" + Math.abs(sufixo.hashCode()));
        medico.setEspecialidade("Clínico Geral");
        medico.setInstituicaoFormacao("UFRGS");
        medico.setDataFormacao(LocalDate.of(2015, 1, 1));
        medico.setObservacoes("Médico de teste");

        entityManager.persist(medico);
        return medico;
    }

    private String cpfValidoPorSufixo(String sufixo) {
        return switch (sufixo) {
            case "medico1" -> "52998224725";
            case "medico2" -> "11144477735";
            case "medico3" -> "12345678909";
            default -> "39053344705";
        };
    }

    private Consulta persistConsulta(Paciente paciente, Medico medico, LocalDateTime dataHora) {
        Consulta consulta = Consulta.builder()
                .dataHora(dataHora)
                .tipo(TipoConsulta.PRESENCIAL)
                .status(StatusConsulta.AGENDADA)
                .meioPagamento(MeioPagamento.PIX)
                .paciente(paciente)
                .medico(medico)
                .pago(false)
                .retorno(false)
                .teleconsulta(false)
                .build();

        entityManager.persist(consulta);
        return consulta;
    }

    private MedicamentoPrescrito persistMedicamento(String nome, Consulta consulta) {
        MedicamentoPrescrito medicamento = MedicamentoPrescrito.builder()
                .nome(nome)
                .dosagem("500mg")
                .frequencia("8/8h")
                .via("Oral")
                .consulta(consulta)
                .build();

        entityManager.persist(medicamento);
        return medicamento;
    }
}