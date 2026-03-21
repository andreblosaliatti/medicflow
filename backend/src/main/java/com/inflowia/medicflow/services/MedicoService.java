package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medico.MedicoComPacientesDTO;
import com.inflowia.medicflow.dto.medico.MedicoDTO;
import com.inflowia.medicflow.dto.medico.MedicoDetailsDTO;
import com.inflowia.medicflow.dto.medico.MedicoMinDTO;
import com.inflowia.medicflow.dto.medico.MedicoUpdateDTO;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.entities.paciente.Endereco;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicoRepository;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Transactional
    public MedicoDetailsDTO cadastrar(MedicoDTO dto) {
        Medico medico = dto.toEntity();
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
