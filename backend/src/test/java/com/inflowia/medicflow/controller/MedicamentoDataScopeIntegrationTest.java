package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.api.ApiPaths;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.domain.usuario.Usuario;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicamentoBaseRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MedicamentoDataScopeIntegrationTest extends AbstractIntegrationTest {

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
    private MedicamentoBaseRepository medicamentoBaseRepository;

    @Autowired
    private MedicamentoPrescritoRepository medicamentoPrescritoRepository;

    private Medico medicoA;
    private Medico medicoB;
    private Usuario admin;
    private Usuario secretaria;
    private Paciente paciente;
    private Consulta consultaA;
    private Consulta consultaB;
    private MedicamentoBase medicamentoBase;
    private MedicamentoPrescrito medicamentoA;
    private MedicamentoPrescrito medicamentoB;
    private String medicoAToken;
    private String adminToken;
    private String secretariaToken;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role medicoRole = roleRepository.save(new Role(null, "ROLE_MEDICO"));
        Role secretariaRole = roleRepository.save(new Role(null, "ROLE_SECRETARIA"));

        medicoA = medicoRepository.save(medico("medico.medicamento.a", "medicamento.a@test.com", "95633733088", "CRM-MED-A", medicoRole));
        medicoB = medicoRepository.save(medico("medico.medicamento.b", "medicamento.b@test.com", "39053344705", "CRM-MED-B", medicoRole));
        admin = usuarioRepository.save(usuario("admin.medicamento", "admin.medicamento@test.com", "32844208606", adminRole));
        secretaria = usuarioRepository.save(usuario("secretaria.medicamento", "secretaria.medicamento@test.com", "61498182097", secretariaRole));

        paciente = pacienteRepository.save(Paciente.builder()
                .nome("Ana")
                .sobrenome("Souza")
                .cpf("52998224725")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .telefone("(11) 99999-0000")
                .email("ana.medicamento@test.com")
                .sexo("F")
                .ativo(true)
                .build());

        medicamentoBase = medicamentoBaseRepository.save(MedicamentoBase.builder()
                .dcb("Dipirona")
                .nomeComercial("Dipirona 500mg")
                .principioAtivo("Dipirona")
                .formaFarmaceutica("Comprimido")
                .dosagemPadrao("500mg")
                .viaAdministracao("Oral")
                .ativo(true)
                .build());

        consultaA = consultaRepository.save(consulta(medicoA, 1));
        consultaB = consultaRepository.save(consulta(medicoB, 2));
        medicamentoA = medicamentoPrescritoRepository.save(medicamentoPrescrito(consultaA));
        medicamentoB = medicamentoPrescritoRepository.save(medicamentoPrescrito(consultaB));

        medicoAToken = tokenFor(medicoA, "ROLE_MEDICO");
        adminToken = tokenFor(admin, "ROLE_ADMIN");
        secretariaToken = tokenFor(secretaria, "ROLE_SECRETARIA");
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void medicoCannotCreatePrescriptionForOtherDoctorsConsulta() throws Exception {
        mockMvc.perform(post(ApiPaths.MEDICAMENTOS + "/consultas/{consultaId}", consultaB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicamentoBaseId": %d,
                                  "dosagem": "500mg",
                                  "frequencia": "8/8h",
                                  "via": "Oral"
                                }
                                """.formatted(medicamentoBase.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoCannotViewUpdateOrDeleteOtherDoctorsPrescription() throws Exception {
        mockMvc.perform(get(ApiPaths.MEDICAMENTOS + "/{id}", medicamentoB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(put(ApiPaths.MEDICAMENTOS + "/{id}", medicamentoB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicamentoBaseId": %d,
                                  "dosagem": "750mg",
                                  "frequencia": "12/12h",
                                  "via": "Oral"
                                }
                                """.formatted(medicamentoBase.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));

        mockMvc.perform(delete(ApiPaths.MEDICAMENTOS + "/{id}", medicamentoB.getId())
                        .header("Authorization", "Bearer " + medicoAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void medicoListEndpointsOnlyReturnOwnPrescriptions() throws Exception {
        expectOnlyMedicoAPrescription(ApiPaths.MEDICAMENTOS + "?nome=Dipirona");
        expectOnlyMedicoAPrescription(ApiPaths.MEDICAMENTOS + "/paciente/" + paciente.getId());
        expectOnlyMedicoAPrescription(ApiPaths.MEDICAMENTOS + "/consultas/" + consultaA.getId());

        mockMvc.perform(get(ApiPaths.MEDICAMENTOS + "/consultas/{consultaId}", consultaB.getId())
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCodes.AUTH_ACCESS_DENIED));
    }

    @Test
    void adminAndSecretariaKeepBroadPrescriptionAccess() throws Exception {
        mockMvc.perform(get(ApiPaths.MEDICAMENTOS + "/{id}", medicamentoB.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(medicamentoB.getId().intValue()));

        mockMvc.perform(get(ApiPaths.MEDICAMENTOS + "?nome=Dipirona")
                        .header("Authorization", "Bearer " + secretariaToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(medicamentoA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(hasItem(medicamentoB.getId().intValue())));

        mockMvc.perform(post(ApiPaths.MEDICAMENTOS + "/consultas/{consultaId}", consultaB.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicamentoBaseId": %d,
                                  "dosagem": "500mg",
                                  "frequencia": "8/8h",
                                  "via": "Oral"
                                }
                                """.formatted(medicamentoBase.getId())))
                .andExpect(status().isCreated());
    }

    @Test
    void medicationBaseCatalogSearchRemainsAvailable() throws Exception {
        mockMvc.perform(get(ApiPaths.MEDICAMENTOS_BASE)
                        .param("q", "Dipi")
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(medicamentoBase.getId().intValue())));
    }

    private void expectOnlyMedicoAPrescription(String path) throws Exception {
        mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + medicoAToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id").value(hasItem(medicamentoA.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id").value(not(hasItem(medicamentoB.getId().intValue()))));
    }

    private Consulta consulta(Medico medico, int daysFromNow) {
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
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        return consulta;
    }

    private MedicamentoPrescrito medicamentoPrescrito(Consulta consulta) {
        return MedicamentoPrescrito.builder()
                .consulta(consulta)
                .medicamentoBase(medicamentoBase)
                .nome("Dipirona")
                .dosagem("500mg")
                .frequencia("8/8h")
                .via("Oral")
                .dataInicio(LocalDate.now())
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
        consultaRepository.deleteAll();
        pacienteRepository.deleteAll();
        medicamentoBaseRepository.deleteAll();
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
