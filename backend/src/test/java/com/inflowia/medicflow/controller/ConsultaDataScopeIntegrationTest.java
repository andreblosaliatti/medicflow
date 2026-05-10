package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.repository.ConsultaRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConsultaDataScopeIntegrationTest extends AbstractIntegrationTest {

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

    private Medico medicoA;
    private Medico medicoB;
    private Usuario admin;
    private Usuario secretaria;
    private Paciente paciente;
    private Consulta consultaA;
    private Consulta consultaB;
    private String medicoAToken;
    private String adminToken;
    private String secretariaToken;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));
        Role secretariaRole = roleRepository.save(new Role(null, "ROLE_SECRETARIA"));

        medicoA = medicoRepository.save(medico("medico.scope.a", "scope.a@test.com", "95633733088", "CRM-A", medicoRole));
        medicoB = medicoRepository.save(medico("medico.scope.b", "scope.b@test.com", "39053344705", "CRM-B", medicoRole));

        admin = usuarioRepository.save(usuario("admin.scope", "admin.scope@test.com", "32844208606", adminRole));
        secretaria = usuarioRepository.save(usuario("secretaria.scope", "secretaria.scope@test.com", "61498182097", secretariaRole));

        paciente = pacienteRepository.save(Paciente.builder()
                .nome("Ana")
                .sobrenome("Souza")
                .cpf("52998224725")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .telefone("(11) 99999-0000")
                .email("ana.souza@test.com")
                .sexo("F")
                .ativo(true)
                .build());

        consultaA = consultaRepository.save(consulta(medicoA, 1));
        consultaB = consultaRepository.save(consulta(medicoB, 2));

        medicoAToken = tokenFor(medicoA, "ROLE_MEDICO");
        adminToken = tokenFor(admin, "ROLE_ADMIN");
        secretariaToken = tokenFor(secretaria, "ROLE_SECRETARIA");
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void medicoCannotViewOtherDoctorsConsultaById() throws Exception {
        mockMvc.perform(get(ApiPaths.CONSULTAS + "/{id}", consultaB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoListAgendaAndTableOnlyIncludeOwnConsultas() throws Exception {
        expectOnlyMedicoAConsulta(ApiPaths.CONSULTAS);
        expectOnlyMedicoAConsulta(ApiPaths.CONSULTAS + "/agenda");
        expectOnlyMedicoAConsulta(ApiPaths.CONSULTAS + "/tabela");
    }

    @Test
    void medicoCannotCreateUsingOtherDoctorsMedicoId() throws Exception {
        mockMvc.perform(post(ApiPaths.CONSULTAS)
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dataHora": "2099-01-01T10:00:00",
                                  "tipo": "PRESENCIAL",
                                  "status": "AGENDADA",
                                  "valorConsulta": 150.00,
                                  "meioPagamento": "PIX",
                                  "pago": false,
                                  "duracaoMinutos": 30,
                                  "retorno": false,
                                  "motivo": "Consulta de rotina",
                                  "pacienteId": %d,
                                  "medicoId": %d
                                }
                                """.formatted(paciente.getId(), medicoB.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoCannotUpdateOrStatusChangeOtherDoctorsConsulta() throws Exception {
        mockMvc.perform(put(ApiPaths.CONSULTAS + "/{id}", consultaB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "motivo": "Tentativa fora do escopo"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(patch(ApiPaths.CONSULTAS + "/{id}/confirmar", consultaB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoCannotReassignOwnConsultaToAnotherDoctor() throws Exception {
        mockMvc.perform(put(ApiPaths.CONSULTAS + "/{id}", consultaA.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicoId": %d
                                }
                                """.formatted(medicoB.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void adminAndSecretariaKeepBroadConsultaAccess() throws Exception {
        mockMvc.perform(get(ApiPaths.CONSULTAS + "/{id}", consultaB.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(consultaB.getId().intValue()))
                .andExpect(jsonPath("$.medicoId").value(medicoB.getId().intValue()));

        mockMvc.perform(get(ApiPaths.CONSULTAS)
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(consultaA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(hasItem(consultaB.getId().intValue())));

        mockMvc.perform(patch(ApiPaths.CONSULTAS + "/{id}/confirmar", consultaB.getId())
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusConsulta.CONFIRMADA.name()));
    }

    private void expectOnlyMedicoAConsulta(String path) throws Exception {
        mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(consultaA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(not(hasItem(consultaB.getId().intValue()))));
    }

    private Consulta consulta(Medico medico, int daysFromNow) {
        Consulta consulta = new Consulta();
        consulta.setDataHora(LocalDateTime.now().plusDays(daysFromNow));
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setValorConsulta(java.math.BigDecimal.valueOf(150));
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPago(false);
        consulta.setDuracaoMinutos(30);
        consulta.setRetorno(false);
        consulta.setMotivo("Consulta de rotina");
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        return consulta;
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
        consultaRepository.deleteAll();
        pacienteRepository.deleteAll();
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
