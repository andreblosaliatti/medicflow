package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.consulta.ConsultaDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDetailsDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaMinDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaUpdateDTO;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicoRepository;
import com.inflowia.medicflow.repositories.PacienteRepository;
import com.inflowia.medicflow.services.exceptions.DatabaseException;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoRepository medicoRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public ConsultaDetailsDTO criar(ConsultaDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));

        Consulta entity = new Consulta();
        copyCreateDtoToEntity(dto, entity, paciente, medico);

        entity = consultaRepository.save(entity);
        return new ConsultaDetailsDTO(entity);
    }

    @Transactional
    public ConsultaDetailsDTO atualizar(Long id, ConsultaUpdateDTO dto) {
        try {
            Consulta entity = consultaRepository.getReferenceById(id);

            Paciente paciente = null;
            Medico medico = null;

            if (dto.getPacienteId() != null) {
                paciente = pacienteRepository.findById(dto.getPacienteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
            }

            if (dto.getMedicoId() != null) {
                medico = medicoRepository.findById(dto.getMedicoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));
            }

            copyUpdateDtoToEntity(dto, entity, paciente, medico);

            entity = consultaRepository.save(entity);
            return new ConsultaDetailsDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Consulta não encontrada");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!consultaRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso encontrado");
        }
        try {
            consultaRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    @Transactional(readOnly = true)
    public ConsultaDetailsDTO buscarPorId(Long id) {
        Consulta entity = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrado"));
        return new ConsultaDetailsDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listar(Pageable pageable) {
        Page<Consulta> page = consultaRepository.findAll(pageable);
        return page.map(ConsultaMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }
        Page<Consulta> page = consultaRepository.findByPacienteId(pacienteId, pageable);
        return page.map(ConsultaMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listarPorMedico(Long medicoId, Pageable pageable) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new ResourceNotFoundException("Médico não encontrado");
        }
        Page<Consulta> page = consultaRepository.findByMedicoId(medicoId, pageable);
        return page.map(ConsultaMinDTO::new);
    }

    @Transactional(readOnly = true)
    public ConsultaDetailsDTO buscarUltimaConsultaPorPaciente(Long pacienteId) {
        Consulta consulta = consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma consulta encontrada para o paciente informado"));
        return new ConsultaDetailsDTO(consulta);
    }

    @Transactional(readOnly = true)
    public List<ConsultaMinDTO> listarPorStatus(StatusConsulta status) {
        List<Consulta> list = consultaRepository.findByStatus(status);
        return list.stream()
                .map(ConsultaMinDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultaMinDTO> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Consulta> list = consultaRepository.findByDataHoraBetween(inicio, fim);
        return list.stream()
                .map(ConsultaMinDTO::new)
                .toList();
    }

    private void copyCreateDtoToEntity(ConsultaDTO dto, Consulta entity,
                                       Paciente paciente, Medico medico) {

        entity.setDataHora(dto.getDataHora());
        entity.setTipo(dto.getTipo());
        entity.setStatus(dto.getStatus());
        entity.setValorConsulta(dto.getValorConsulta());
        entity.setMeioPagamento(dto.getMeioPagamento());
        entity.setPago(dto.getPago());
        entity.setDataPagamento(dto.getDataPagamento());
        entity.setDuracaoMinutos(dto.getDuracaoMinutos());
        entity.setRetorno(dto.isRetorno());
        entity.setDataLimiteRetorno(dto.getDataLimiteRetorno());
        entity.setTeleconsulta(dto.isTeleconsulta());
        entity.setLinkAcesso(dto.getLinkAcesso());
        entity.setPlanoSaude(dto.getPlanoSaude());
        entity.setNumeroCarteirinha(dto.getNumeroCarteirinha());
        entity.setMotivo(dto.getMotivo());
        entity.setAnamnese(dto.getAnamnese());
        entity.setExameFisico(dto.getExameFisico());
        entity.setDiagnostico(dto.getDiagnostico());
        entity.setPrescricao(dto.getPrescricao());
        entity.setObservacoes(dto.getObservacoes());
        entity.setPaciente(paciente);
        entity.setMedico(medico);
    }

    private void copyUpdateDtoToEntity(ConsultaUpdateDTO dto, Consulta entity,
                                       Paciente paciente, Medico medico) {

        if (dto.getDataHora() != null) {
            entity.setDataHora(dto.getDataHora());
        }
        if (dto.getTipo() != null) {
            entity.setTipo(dto.getTipo());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getValorConsulta() != null) {
            entity.setValorConsulta(dto.getValorConsulta());
        }
        if (dto.getMeioPagamento() != null) {
            entity.setMeioPagamento(dto.getMeioPagamento());
        }
        if (dto.getPago() != null) {
            entity.setPago(dto.getPago());
        }
        if (dto.getDataPagamento() != null) {
            entity.setDataPagamento(dto.getDataPagamento());
        }
        if (dto.getDuracaoMinutos() != null) {
            entity.setDuracaoMinutos(dto.getDuracaoMinutos());
        }

        // ⚠ aqui é importante: no DTO, use Boolean, não boolean
        if (dto.getRetorno() != null) {
            entity.setRetorno(dto.getRetorno());
        }
        if (dto.getDataLimiteRetorno() != null) {
            entity.setDataLimiteRetorno(dto.getDataLimiteRetorno());
        }
        if (dto.getTeleconsulta() != null) {
            entity.setTeleconsulta(dto.getTeleconsulta());
        }

        if (dto.getLinkAcesso() != null) {
            entity.setLinkAcesso(dto.getLinkAcesso());
        }
        if (dto.getPlanoSaude() != null) {
            entity.setPlanoSaude(dto.getPlanoSaude());
        }
        if (dto.getNumeroCarteirinha() != null) {
            entity.setNumeroCarteirinha(dto.getNumeroCarteirinha());
        }
        if (dto.getMotivo() != null) {
            entity.setMotivo(dto.getMotivo());
        }
        if (dto.getAnamnese() != null) {
            entity.setAnamnese(dto.getAnamnese());
        }
        if (dto.getExameFisico() != null) {
            entity.setExameFisico(dto.getExameFisico());
        }
        if (dto.getDiagnostico() != null) {
            entity.setDiagnostico(dto.getDiagnostico());
        }
        if (dto.getPrescricao() != null) {
            entity.setPrescricao(dto.getPrescricao());
        }
        if (dto.getObservacoes() != null) {
            entity.setObservacoes(dto.getObservacoes());
        }

        if (paciente != null) {
            entity.setPaciente(paciente);
        }
        if (medico != null) {
            entity.setMedico(medico);
        }
    }

}
