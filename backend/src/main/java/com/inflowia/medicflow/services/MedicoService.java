package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medico.*;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.entities.paciente.Endereco;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicoRepository;
import com.inflowia.medicflow.services.exceptions.DatabaseException;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
        // aqui você poderia validar duplicidade de CRM, email, etc
        Medico medico = dto.toEntity();
        Medico salvo = repository.save(medico);
        return new MedicoDetailsDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<MedicoMinDTO> listar(Pageable pageable) {
        Page<Medico> page = repository.findAll(pageable);
        return page.map(MedicoMinDTO::new);
    }


    @Transactional(readOnly = true)
    public MedicoDetailsDTO buscarPorId(Long id) {
        Medico medico = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));
        return new MedicoDetailsDTO(medico);
    }

    @Transactional(readOnly = true)
    public List<MedicoComPacientesDTO> listarTodosMedicosComPacientes() {
        List<Medico> medicos = repository.findAll();

        return medicos.stream()
                .map(medico -> {
                    var pacientes = consultaRepository.findPacientesDistinctByMedicoId(medico.getId());
                    return new MedicoComPacientesDTO(medico, pacientes);
                })
                .toList();
    }

    @Transactional
    public MedicoDetailsDTO atualizar(Long id, MedicoUpdateDTO dto) {
        Medico medico = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));

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

        return new MedicoDetailsDTO(medico);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Médico não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível excluir médico com consultas cadastradas. Utilize a inativação (soft delete).");
        }
    }

    @Transactional(readOnly = true)
    public MedicoComPacientesDTO buscarMedicoComPacientes(Long medicoId) {
        Medico medico = repository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));

        List<Paciente> pacientes = consultaRepository.findPacientesDistinctByMedicoId(medicoId);

        return new MedicoComPacientesDTO(medico, pacientes);
    }

    @Transactional(readOnly = true)
    public List<PacienteMinDTO> listarPacientesPorMedico(Long medicoId) {
        if (!repository.existsById(medicoId)) {
            throw new ResourceNotFoundException("Médico não encontrado");
        }

        List<Paciente> pacientes = consultaRepository.findPacientesDistinctByMedicoId(medicoId);

        return pacientes.stream()
                .map(PacienteMinDTO::new)
                .toList();
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
