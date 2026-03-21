package com.inflowia.medicflow.service;

import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Transactional
    public PacienteDTO cadastrar(PacienteDTO dto) {
        Paciente entidade = new Paciente();
        copiarDtoParaEntidade(dto, entidade);
        entidade.setAtivo(true);

        Paciente salvo = repository.save(entidade);
        return new PacienteDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<PacienteMinDTO> listar(Pageable pageable) {
        Page<Paciente> page = repository.findAllByAtivoTrue(pageable);
        return page.map(PacienteMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<PacienteMinDTO> listarInativos(Pageable pageable) {
        Page<Paciente> page = repository.findAllByAtivoFalse(pageable);
        return page.map(PacienteMinDTO::new);
    }

    @Transactional(readOnly = true)
    public PacienteDTO buscarPorId(Long id) {
        Paciente paciente = getPacienteAtivo(id);
        return new PacienteDTO(paciente);
    }

    @Transactional
    public PacienteDTO atualizar(Long id, PacienteUpdateDTO dto) {
        Paciente paciente = getPacienteAtivo(id);
        copiarUpdateDtoParaEntidade(dto, paciente);

        Paciente atualizado = repository.save(paciente);
        return new PacienteDTO(atualizado);
    }

    @Transactional
    public void delete(Long id) {
        Paciente paciente = getPacienteAtivo(id);
        paciente.setAtivo(false);
        repository.save(paciente);
    }

    @Transactional
    public void softDelete(Long id) {
        delete(id);
    }

    private Paciente getPacienteAtivo(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Paciente")));
    }

    private void copiarDtoParaEntidade(PacienteDTO dto, Paciente entidade) {
        entidade.setPrimeiroNome(dto.getPrimeiroNome());
        entidade.setSobrenome(dto.getSobrenome());
        entidade.setCpf(dto.getCpf());
        entidade.setDataNascimento(dto.getDataNascimento());
        entidade.setTelefone(dto.getTelefone());
        entidade.setEmail(dto.getEmail());
        entidade.setSexo(dto.getSexo());

        if (dto.getEndereco() != null) {
            entidade.setEndereco(dto.getEndereco().toEntity());
        }
    }

    private void copiarUpdateDtoParaEntidade(PacienteUpdateDTO dto, Paciente entidade) {
        if (dto.getPrimeiroNome() != null) {
            entidade.setPrimeiroNome(dto.getPrimeiroNome());
        }

        if (dto.getSobrenome() != null) {
            entidade.setSobrenome(dto.getSobrenome());
        }

        if (dto.getTelefone() != null) {
            entidade.setTelefone(dto.getTelefone());
        }

        if (dto.getEmail() != null) {
            entidade.setEmail(dto.getEmail());
        }

        if (dto.getSexo() != null) {
            entidade.setSexo(dto.getSexo());
        }

        if (dto.getEndereco() != null) {
            entidade.setEndereco(dto.getEndereco().toEntity());
        }
    }
}
