package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.repositories.PacienteRepository;
import com.inflowia.medicflow.services.exceptions.DatabaseException;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    // POST - cadastrar
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
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        return new PacienteDTO(paciente);
    }

    // PUT - atualizar
    @Transactional
    public PacienteDTO atualizar(Long id, PacienteUpdateDTO dto) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        copiarUpdateDtoParaEntidade(dto, paciente);

        Paciente atualizado = repository.save(paciente);
        return new PacienteDTO(atualizado);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    // DELETE - desativar (soft delete)
    @Transactional
    public void softDelete(Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        paciente.setAtivo(false);
        repository.save(paciente);
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
