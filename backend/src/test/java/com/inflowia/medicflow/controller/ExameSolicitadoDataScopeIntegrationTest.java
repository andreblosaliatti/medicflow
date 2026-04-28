package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.exame.ExameBase;
import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.exame.StatusExame;
import com.inflowia.medicflow.domain.exame.TipoExame;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.ExameBaseRepository;
import com.inflowia.medicflow.repository.ExameSolicitadoRepository;
import com.inflowia.medicflow.repository.MedicoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.repository.UsuarioRepository;
import com.inflowia.medicflow.security.JwtService;
import com.inflowia.medicflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExameSolicitadoDataScopeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ExameBaseRepository exameBaseRepository;

    @Autowired
    private ExameSolicitadoRepository exameSolicitadoRepository;

    private Medico medicoA;
    private Medico medicoB;
    private Usuario admin;
    private Usuario secretaria;
    private Paciente paciente;
    private Consulta consultaA;
    private Consulta consultaB;
    private ExameBase exameBase;
    private ExameSolicitado exameA;
    private ExameSolicitado exameB;
    private String medicoAToken;
    private String adminToken;
    private String secretariaToken;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));
        Role secretariaRole = roleRepository.save(new Role(null, "ROLE_SECRETARIA"));

        medicoA = medicoRepository.save(medico("medico.exame.a", "exame.a@test.com", "95633733088", "CRM-EX-A", medicoRole));
        medicoB = medicoRepository.save(medico("medico.exame.b", "exame.b@test.com", "39053344705", "CRM-EX-B", medicoRole));
        admin = usuarioRepository.save(usuario("admin.exame", "admin.exame@test.com", "32844208606", adminRole));
        secretaria = usuarioRepository.save(usuario("secretaria.exame", "secretaria.exame@test.com", "61498182097", secretariaRole));

        paciente = pacienteRepository.save(Paciente.builder()
                .nome("Ana")
                .sobrenome("Souza")
                .cpf("52998224725")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .telefone("(11) 99999-0000")
                .email("ana.exame@test.com")
                .sexo("F")
                .ativo(true)
                .build());

        exameBase = exameBaseRepository.save(ExameBase.builder()
                .nome("Hemograma")
                .codigoTuss("40304361")
                .tipo(TipoExame.LABORATORIAL)
                .prazoEstimado(2)
                .build());

        consultaA = consultaRepository.save(consulta(medicoA, 1));
        consultaB = consultaRepository.save(consulta(medicoB, 2));
        exameA = exameSolicitadoRepository.save(exameSolicitado(consultaA));
        exameB = exameSolicitadoRepository.save(exameSolicitado(consultaB));

        medicoAToken = tokenFor(medicoA, "ROLE_MEDICO");
        adminToken = tokenFor(admin, "ROLE_ADMIN");
        secretariaToken = tokenFor(secretaria, "ROLE_SECRETARIA");
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void medicoCannotCreateExamRequestForOtherDoctorsConsulta() throws Exception {
        mockMvc.perform(post(ApiPaths.EXAMES_SOLICITADOS)
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "consultaId": %d,
                                  "exameBaseId": %d,
                                  "status": "SOLICITADO",
                                  "justificativa": "Controle clinico"
                                }
                                """.formatted(consultaB.getId(), exameBase.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoCannotViewUpdateOrDeleteOtherDoctorsExamRequest() throws Exception {
        mockMvc.perform(get(ApiPaths.EXAMES_SOLICITADOS + "/{id}", exameB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(put(ApiPaths.EXAMES_SOLICITADOS + "/{id}", exameB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CANCELADO",
                                  "observacoes": "Fora do escopo"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(delete(ApiPaths.EXAMES_SOLICITADOS + "/{id}", exameB.getId())
                        .header("Authorization", "Bearer " + medicoAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoListEndpointsOnlyReturnOwnExamRequests() throws Exception {
        expectOnlyMedicoAExam(ApiPaths.EXAMES_SOLICITADOS + "/exame-base/" + exameBase.getId());
        expectOnlyMedicoAExam(ApiPaths.EXAMES_SOLICITADOS + "/paciente/" + paciente.getId());
        expectOnlyMedicoAExam(ApiPaths.EXAMES_SOLICITADOS + "/paciente/" + paciente.getId() + "/ultima-consulta");
        expectOnlyMedicoAExam(ApiPaths.EXAMES_SOLICITADOS + "/consulta/" + consultaA.getId());

        mockMvc.perform(get(ApiPaths.EXAMES_SOLICITADOS + "/consulta/{consultaId}", consultaB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void adminAndSecretariaKeepBroadReadAccess() throws Exception {
        mockMvc.perform(get(ApiPaths.EXAMES_SOLICITADOS + "/{id}", exameB.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exameB.getId().intValue()));

        mockMvc.perform(get(ApiPaths.EXAMES_SOLICITADOS + "/exame-base/{exameBaseId}", exameBase.getId())
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(exameA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(hasItem(exameB.getId().intValue())));
    }

    private void expectOnlyMedicoAExam(String path) throws Exception {
        mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(exameA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(not(hasItem(exameB.getId().intValue()))));
    }

    private Consulta consulta(Medico medico, int daysFromNow) {
        Consulta consulta = new Consulta();
        consulta.setDataHora(LocalDateTime.now().plusDays(daysFromNow));
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setValorConsulta(BigDecimal.valueOf(150));
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPago(false);
        consulta.setDuracaoMinutos(30);
        consulta.setRetorno(false);
        consulta.setMotivo("Consulta de rotina");
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        return consulta;
    }

    private ExameSolicitado exameSolicitado(Consulta consulta) {
        return ExameSolicitado.builder()
                .consulta(consulta)
                .exameBase(exameBase)
                .status(StatusExame.SOLICITADO)
                .justificativa("Controle clinico")
                .build();
    }

    private Medico medico(String login, String email, String cpf, String crm, Role role) {
        Medico medico = new Medico();
        medico.setLogin(login);
        medico.setSenha(passwordEncoder.encode("secret123"));
        medico.setNome("Medico");
        medico.setSobrenome(crm);
        medico.setEmail(email);
        medico.setCpf(cpf);
        medico.setAtivo(true);
        medico.setRoles(Set.of(role));
        medico.setCrm(crm);
        medico.setEspecialidade("Clinica geral");
        return medico;
    }

    private Usuario usuario(String login, String email, String cpf, Role role) {
        return Usuario.builder()
                .login(login)
                .senha(passwordEncoder.encode("secret123"))
                .nome("Usuario")
                .sobrenome(login)
                .email(email)
                .cpf(cpf)
                .ativo(true)
                .roles(Set.of(role))
                .build();
    }

    private String tokenFor(Usuario usuario, String authority) {
        return jwtService.generateToken(User.withUsername(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities(authority)
                .build());
    }

    private void cleanDatabase() {
        exameSolicitadoRepository.deleteAll();
        consultaRepository.deleteAll();
        pacienteRepository.deleteAll();
        exameBaseRepository.deleteAll();
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
