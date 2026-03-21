package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.dto.medico.MedicoDTO;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicoRepository;
import com.inflowia.medicflow.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    private MedicoRepository repository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MedicoService service;

    @Test
    void cadastrarShouldEncodePasswordAndResolveRoles() {
        MedicoDTO dto = MedicoDTO.builder()
                .login("medico")
                .senha("plain123")
                .nome("Ana")
                .sobrenome("Silva")
                .email("ana@test.com")
                .cpf("32844208606")
                .crm("12345")
                .especialidade("Cardiologia")
                .roles(Set.of("MEDICO", "ADMIN"))
                .build();

        Role medicoRole = new Role(1L, "ROLE_MEDICO");
        Role adminRole = new Role(2L, "ROLE_ADMIN");

        when(passwordEncoder.encode("plain123")).thenReturn("encoded");
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByAuthority("ROLE_MEDICO")).thenReturn(Optional.of(medicoRole));
        when(repository.save(any(Medico.class))).thenAnswer(invocation -> {
            Medico medico = invocation.getArgument(0);
            medico.setId(10L);
            return medico;
        });

        var result = service.cadastrar(dto);

        ArgumentCaptor<Medico> captor = ArgumentCaptor.forClass(Medico.class);
        verify(repository).save(captor.capture());
        Medico saved = captor.getValue();
        assertEquals("encoded", saved.getSenha());
        assertEquals(Set.of(medicoRole, adminRole), saved.getRoles());
        assertTrue(saved.isAtivo());
        assertEquals("Ana Silva", result.getNomeCompleto());
    }

    @Test
    void listarResumoShouldHonorLimitAndMapResult() {
        Medico medico = new Medico();
        medico.setId(1L);
        medico.setNome("Ana");
        medico.setSobrenome("Silva");
        medico.setCrm("CRM-1");
        medico.setEspecialidade("Cardiologia");
        medico.setAtivo(true);

        when(repository.searchActiveForSelect("ana", Pageable.ofSize(5))).thenReturn(List.of(medico));

        var result = service.listarResumo("ana", 5);

        assertEquals(1, result.size());
        assertEquals("Ana Silva", result.getFirst().getNomeCompleto());
    }

    @Test
    void deleteShouldInactivateDoctor() {
        Medico medico = new Medico();
        medico.setId(1L);
        medico.setAtivo(true);

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(medico));

        service.delete(1L);

        assertFalse(medico.isAtivo());
        verify(repository).save(medico);
    }
}
