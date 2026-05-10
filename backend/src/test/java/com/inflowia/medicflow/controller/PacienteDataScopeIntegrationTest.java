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
import com.inflowia.medicflow.repository.ExameSolicitadoRepository;
import com.inflowia.medicflow.repository.MedicamentoPrescritoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PacienteDataScopeIntegrationTest extends AbstractIntegrationTest {

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
    private ExameSolicitadoRepository exameSolicitadoRepository;

    @Autowired
    private MedicamentoPrescritoRepository medicamentoPrescritoRepository;

    private Medico medicoA;
    private Medico medicoB;
    private Usuario admin;
    private Usuario secretaria;
    private Paciente pacienteA;
    private Paciente pacienteB;
    private String medicoAToken;
    private String adminToken;
    private String secretariaToken;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));
        Role secretariaRole = roleRepository.save(new Role(null, "ROLE_SECRETARIA"));

        medicoA = medicoRepository.save(medico("medico.paciente.a", "paciente.medico.a@test.com", "95633733088", "CRM-PAC-A", medicoRole));
        medicoB = medicoRepository.save(medico("medico.paciente.b", "paciente.medico.b@test.com", "39053344705", "CRM-PAC-B", medicoRole));
        admin = usuarioRepository.save(usuario("admin.paciente", "admin.paciente@test.com", "32844208606", adminRole));
        secretaria = usuarioRepository.save(usuario("secretaria.paciente", "secretaria.paciente@test.com", "61498182097", secretariaRole));

        pacienteA = pacienteRepository.save(paciente("Ana", "Souza", "52998224725", "ana.paciente@test.com"));
        pacienteB = pacienteRepository.save(paciente("Bruno", "Lima", "11144477735", "bruno.paciente@test.com"));

        consultaRepository.save(consulta(pacienteA, medicoA, 1));
        consultaRepository.save(consulta(pacienteB, medicoB, 2));

        medicoAToken = tokenFor(medicoA, "ROLE_MEDICO");
        adminToken = tokenFor(admin, "ROLE_ADMIN");
        secretariaToken = tokenFor(secretaria, "ROLE_SECRETARIA");
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void medicoPatientListOnlyIncludesPatientsLinkedToOwnConsultas() throws Exception {
        mockMvc.perform(get(ApiPaths.PACIENTES)
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(pacienteA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(not(hasItem(pacienteB.getId().intValue()))));
    }

    @Test
    void medicoCanViewOwnLinkedPatient() throws Exception {
        mockMvc.perform(get(ApiPaths.PACIENTES + "/{id}", pacienteA.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pacienteA.getId().intValue()));
    }

    @Test
    void medicoCannotViewProfileOrProntuarioOfOtherDoctorsPatient() throws Exception {
        mockMvc.perform(get(ApiPaths.PACIENTES + "/{id}", pacienteB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(get(ApiPaths.PACIENTES + "/{id}/perfil", pacienteB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(get(ApiPaths.PACIENTES + "/{id}/prontuario", pacienteB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoCannotMutateOtherDoctorsPatient() throws Exception {
        mockMvc.perform(put(ApiPaths.PACIENTES + "/{id}", pacienteB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "telefone": "(11) 98888-0000"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(delete(ApiPaths.PACIENTES + "/{id}", pacienteB.getId())
                        .header("Authorization", "Bearer " + medicoAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(patch(ApiPaths.PACIENTES + "/{id}/inativar", pacienteB.getId())
                        .header("Authorization", "Bearer " + medicoAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void adminAndSecretariaKeepBroadPatientAccess() throws Exception {
        mockMvc.perform(get(ApiPaths.PACIENTES)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(pacienteA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(hasItem(pacienteB.getId().intValue())));

        mockMvc.perform(get(ApiPaths.PACIENTES + "/{id}/perfil", pacienteB.getId())
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pacienteB.getId().intValue()));
    }

    private Consulta consulta(Paciente paciente, Medico medico, int daysFromNow) {
        Consulta consulta = new Consulta();
        consulta.setDataHora(LocalDateTime.now().plusDays(daysFromNow));
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consulta.setValorConsulta(BigDecimal.valueOf(150));
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPago(false);
        consulta.setDuracaoMinutos(30);
        consulta.setRetorno(false);
        consulta.setMotivo("Consulta de rotina");
        consulta.setDiagnostico("Acompanhamento clinico");
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        return consulta;
    }

    private Paciente paciente(String nome, String sobrenome, String cpf, String email) {
        return Paciente.builder()
                .nome(nome)
                .sobrenome(sobrenome)
                .cpf(cpf)
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .telefone("(11) 99999-0000")
                .email(email)
                .sexo("F")
                .ativo(true)
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
        medicamentoPrescritoRepository.deleteAll();
        exameSolicitadoRepository.deleteAll();
        consultaRepository.deleteAll();
        pacienteRepository.deleteAll();
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
