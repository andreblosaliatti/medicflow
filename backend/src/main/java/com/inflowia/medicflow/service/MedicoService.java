package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.paciente.Endereco;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.domain.usuario.Role;
import com.inflowia.medicflow.dto.medico.MedicoComPacientesDTO;
import com.inflowia.medicflow.dto.medico.MedicoDTO;
import com.inflowia.medicflow.dto.medico.MedicoDetailsDTO;
import com.inflowia.medicflow.dto.medico.MedicoMinDTO;
import com.inflowia.medicflow.dto.medico.MedicoSelectDTO;
import com.inflowia.medicflow.dto.medico.MedicoUpdateDTO;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicoRepository;
import com.inflowia.medicflow.repository.RoleRepository;
import com.inflowia.medicflow.security.AccessRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MedicoDetailsDTO cadastrar(MedicoDTO dto) {
        Medico medico = dto.toEntity();
        medico.setSenha(passwordEncoder.encode(dto.getSenha()));
        medico.setRoles(resolveRoles(dto.getRoles()));
        medico.setAtivo(true);
        Medico salvo = repository.save(medico);
        return new MedicoDetailsDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<MedicoMinDTO> listar(Pageable pageable) {
        Page<Medico> page = repository.findByAtivoTrue(pageable);
        return page.map(MedicoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public List<MedicoSelectDTO> listarResumo(String termo, int limite) {
        PageRequest pageRequest = PageRequest.of(0, Math.max(1, Math.min(limite, 50)));
        return repository.searchActiveForSelect(termo, pageRequest)
                .stream()
                .map(MedicoSelectDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicoDetailsDTO buscarPorId(Long id) {
        Medico medico = getMedicoAtivo(id);
        return new MedicoDetailsDTO(medico);
    }

    @Transactional(readOnly = true)
    public List<MedicoComPacientesDTO> listarTodosMedicosComPacientes() {
        List<Medico> medicos = repository.findAllByAtivoTrue();

        return medicos.stream()
                .map(medico -> {
                    var pacientes = consultaRepository.findPacientesDistinctByMedicoId(medico.getId());
                    return new MedicoComPacientesDTO(medico, pacientes);
                })
                .toList();
    }

    @Transactional
    public MedicoDetailsDTO atualizar(Long id, MedicoUpdateDTO dto) {
        Medico medico = getMedicoAtivo(id);

        if (dto.getNome() != null) medico.setNome(dto.getNome());
        if (dto.getSobrenome() != null) medico.setSobrenome(dto.getSobrenome());
        if (dto.getEmail() != null) medico.setEmail(dto.getEmail());
        if (dto.getEspecialidade() != null) medico.setEspecialidade(dto.getEspecialidade());
        if (dto.getInstituicaoFormacao() != null) medico.setInstituicaoFormacao(dto.getInstituicaoFormacao());
        if (dto.getDataFormacao() != null) medico.setDataFormacao(dto.getDataFormacao());
        if (dto.getSexo() != null) medico.setSexo(dto.getSexo());
        if (dto.getObservacoes() != null) medico.setObservacoes(dto.getObservacoes());

        if (dto.getEndereco() != null) {
            medico.setEndereco(dto.getEndereco().toEntity());
        }

        repository.save(medico);
        return new MedicoDetailsDTO(medico);
    }

    @Transactional
    public void delete(Long id) {
        Medico medico = getMedicoAtivo(id);
        medico.setAtivo(false);
        repository.save(medico);
    }

    @Transactional(readOnly = true)
    public MedicoComPacientesDTO buscarMedicoComPacientes(Long medicoId) {
        Medico medico = getMedicoAtivo(medicoId);
        List<Paciente> pacientes = consultaRepository.findPacientesDistinctByMedicoId(medicoId);
        return new MedicoComPacientesDTO(medico, pacientes);
    }

    @Transactional(readOnly = true)
    public List<PacienteMinDTO> listarPacientesPorMedico(Long medicoId) {
        if (!repository.existsByIdAndAtivoTrue(medicoId)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Médico"));
        }

        List<Paciente> pacientes = consultaRepository.findPacientesDistinctByMedicoId(medicoId);

        return pacientes.stream()
                .map(PacienteMinDTO::new)
                .toList();
    }

    private Medico getMedicoAtivo(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Médico")));
    }

    private Set<Role> resolveRoles(Set<String> roleAuthorities) {
        if (roleAuthorities == null || roleAuthorities.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Role> resolvedRoles = new HashSet<>();

        for (String authority : AccessRole.toCanonicalAuthorities(roleAuthorities)) {
            Role role = roleRepository.findByAuthority(authority)
                    .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + authority));
            resolvedRoles.add(role);
        }

        return resolvedRoles;
    }

    private void copiarUpdateDtoParaEntidade(MedicoUpdateDTO dto, Medico medico) {
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            medico.setNome(dto.getNome());
        }
        if (dto.getSobrenome() != null && !dto.getSobrenome().isBlank()) {
            medico.setSobrenome(dto.getSobrenome());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            medico.setEmail(dto.getEmail());
        }
        if (dto.getEspecialidade() != null && !dto.getEspecialidade().isBlank()) {
            medico.setEspecialidade(dto.getEspecialidade());
        }
        if (dto.getInstituicaoFormacao() != null && !dto.getInstituicaoFormacao().isBlank()) {
            medico.setInstituicaoFormacao(dto.getInstituicaoFormacao());
        }
        if (dto.getDataFormacao() != null) {
            medico.setDataFormacao(dto.getDataFormacao());
        }
        if (dto.getSexo() != null && !dto.getSexo().isBlank()) {
            medico.setSexo(dto.getSexo());
        }
        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank()) {
            medico.setObservacoes(dto.getObservacoes());
        }
        if (dto.getEndereco() != null) {
            Endereco novoEndereco = dto.toEnderecoEntity();
            medico.setEndereco(novoEndereco);
        }
    }
}
