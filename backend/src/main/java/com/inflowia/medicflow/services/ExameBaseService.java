package com.inflowia.medicflow.services.exame;

import com.inflowia.medicflow.dto.exame.*;
import com.inflowia.medicflow.entities.exame.ExameBase;
import com.inflowia.medicflow.repositories.ExameBaseRepository;
import com.inflowia.medicflow.services.exceptions.DatabaseException;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExameBaseService {

    @Autowired
    private ExameBaseRepository repository;

    @Transactional(readOnly = true)
    public Page<ExameBaseMinDTO> findAll(String nome, Pageable pageable) {
        Page<ExameBase> page;
        if (nome == null || nome.trim().isEmpty()) {
            page = repository.findAll(pageable);
        } else {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        }
        return page.map(ExameBaseMinDTO::new);
    }

    @Transactional(readOnly = true)
    public ExameBaseDetailsDTO findById(Long id) {
        ExameBase entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exame base não encontrado"));
        return new ExameBaseDetailsDTO(entity);
    }

    @Transactional
    public ExameBaseDetailsDTO create(ExameBaseDTO dto) {
        ExameBase entity = new ExameBase();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ExameBaseDetailsDTO(entity);
    }


    @Transactional
    public ExameBaseDetailsDTO update(Long id, ExameBaseUpdateDTO dto) {
        try {
            ExameBase entity = repository.getReferenceById(id);

            if (dto.getNome() != null) {
                entity.setNome(dto.getNome());
            }
            if (dto.getCodigoTuss() != null) {
                entity.setCodigoTuss(dto.getCodigoTuss());
            }
            if (dto.getTipo() != null) {
                entity.setTipo(dto.getTipo());
            }
            if (dto.getPrazoEstimado() != null) {
                entity.setPrazoEstimado(dto.getPrazoEstimado());
            }

            entity = repository.save(entity);
            return new ExameBaseDetailsDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Exame base não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Exame base não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível excluir exame base com exames solicitados. Utilize a inativação (soft delete).");
        }
    }

    private void copyDtoToEntity(ExameBaseDTO dto, ExameBase entity) {
        entity.setNome(dto.getNome());
        entity.setCodigoTuss(dto.getCodigoTuss());
        entity.setTipo(dto.getTipo());
        entity.setPrazoEstimado(dto.getPrazoEstimado());
    }
}
