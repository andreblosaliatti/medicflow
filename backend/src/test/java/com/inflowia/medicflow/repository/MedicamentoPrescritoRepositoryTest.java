package com.inflowia.medicflow.repository;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
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
    void findByConsultaPacienteIdShouldNotMixOtherPaciente() {

        Paciente paciente1 = criarPaciente("Ana", "Souza", "52998224725");
        Paciente paciente2 = criarPaciente("Carlos", "Oliveira", "39053344705");

        Medico medico = criarMedico();

        Consulta c1 = criarConsulta(paciente1, medico);
        Consulta c2 = criarConsulta(paciente2, medico);

        MedicamentoBase base = criarMedicamento("Losartana");

        repository.save(MedicamentoPrescrito.builder()
                .consulta(c1)
                .medicamentoBase(base)
                .nome("Losartana")
                .dosagem("50mg")
                .frequencia("1x dia")
                .via("VO")
                .dataInicio(LocalDate.now()) // 🔥 obrigatório
                .ativo(true)
                .build());

        repository.save(MedicamentoPrescrito.builder()
                .consulta(c2)
                .medicamentoBase(base)
                .nome("Outro")
                .dosagem("10mg")
                .frequencia("1x dia")
                .via("VO")
                .dataInicio(LocalDate.now())
                .ativo(true)
                .build());

        var result = repository.findByConsultaPacienteId(
                paciente1.getId(),
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
    }

    // ===== FACTORIES =====

    private Paciente criarPaciente(String nome, String sobrenome, String cpf) {
        return pacienteRepository.save(Paciente.builder()
                .nome(nome)
                .sobrenome(sobrenome)
                .cpf(cpf)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .sexo("M")
                .ativo(true)
                .build());
    }

    private Consulta criarConsulta(Paciente paciente, Medico medico) {
        return consultaRepository.save(Consulta.builder()
                .paciente(paciente)
                .medico(medico)
                .meioPagamento(MeioPagamento.PIX)
                .status(StatusConsulta.AGENDADA)
                .tipo(TipoConsulta.PRESENCIAL)
                .retorno(false)
                .dataHora(LocalDateTime.now())
                .build());
    }

    private Medico criarMedico() {
        String suf = UUID.randomUUID().toString().substring(0, 5);

        Medico m = new Medico();
        m.setLogin("med_" + suf);
        m.setSenha("123");
        m.setNome("Dr");
        m.setSobrenome("Teste");
        m.setEmail("med" + suf + "@test.com");
        m.setCpf("11144477735");
        m.setAtivo(true);
        m.setCrm("CRM" + suf);
        m.setEspecialidade("Clinico");

        return medicoRepository.save(m);
    }

    private MedicamentoBase criarMedicamento(String nome) {
        return medicamentoBaseRepository.save(
                MedicamentoBase.builder()
                        .nomeComercial(nome)
                        .ativo(true)
                        .controlado(false)
                        .build()
        );
    }
}