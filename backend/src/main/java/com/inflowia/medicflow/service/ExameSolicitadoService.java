package com.inflowia.medicflow.service;

import com.inflowia.medicflow.dto.exame.ExameSolicitadoDetailsDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoMinDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoPatchDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoUpdateDTO;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.exame.ExameBase;
import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.exame.StatusExame;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.ExameBaseRepository;
import com.inflowia.medicflow.repository.ExameSolicitadoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
public class ExameSolicitadoService {

    @Autowired
    private ExameSolicitadoRepository exameSolicitadoRepository;

    @Autowired
    private ExameBaseRepository exameBaseRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    // CREATE
    @Transactional
    public ExameSolicitadoDetailsDTO inserir(ExameSolicitadoUpdateDTO dto) {
        ExameSolicitado entity = new ExameSolicitado();
        copyDtoToEntity(dto, entity);
        entity = exameSolicitadoRepository.save(entity);
        return new ExameSolicitadoDetailsDTO(entity);
    }

    // DELETE
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!exameSolicitadoRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCodes.EXAME_SOLICITADO_NOT_FOUND, ExceptionMessages.notFound("Exame solicitado"));
        }
        try {
            exameSolicitadoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException(ErrorCodes.EXAME_SOLICITADO_BUSINESS_RULE, "Não é possível excluir o exame solicitado informado porque ele está vinculado a outros registros. Utilize a inativação.");
        }
    }

    // FIND BY ID
    @Transactional(readOnly = true)
    public ExameSolicitadoDetailsDTO buscarPorId(Long id) {
        ExameSolicitado entity = exameSolicitadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.EXAME_SOLICITADO_NOT_FOUND, ExceptionMessages.notFound("Exame solicitado")));

        return new ExameSolicitadoDetailsDTO(entity);
    }

    @Transactional
    public ExameSolicitadoDetailsDTO atualizar(Long id, ExameSolicitadoPatchDTO dto) {
        ExameSolicitado entity = exameSolicitadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.EXAME_SOLICITADO_NOT_FOUND, ExceptionMessages.notFound("Exame solicitado")));

        validatePatch(entity, dto);
        entity.setStatus(dto.getStatus());
        entity.setDataColeta(dto.getDataColeta());
        entity.setDataResultado(dto.getDataResultado());
        entity.setObservacoes(dto.getObservacoes());

        entity = exameSolicitadoRepository.save(entity);
        return new ExameSolicitadoDetailsDTO(entity);
    }

    @Transactional
    public ExameSolicitadoDetailsDTO atualizarParcialmente(Long id, ExameSolicitadoPatchDTO dto) {
        ExameSolicitado entity = exameSolicitadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.EXAME_SOLICITADO_NOT_FOUND, ExceptionMessages.notFound("Exame solicitado")));

        validatePatch(entity, dto);

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getDataColeta() != null) {
            entity.setDataColeta(dto.getDataColeta());
        }
        if (dto.getDataResultado() != null) {
            entity.setDataResultado(dto.getDataResultado());
        }
        if (dto.getObservacoes() != null) {
            entity.setObservacoes(dto.getObservacoes());
        }

        entity = exameSolicitadoRepository.save(entity);
        return new ExameSolicitadoDetailsDTO(entity);
    }

    // LISTAR EXAMES DE UMA CONSULTA
    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorConsulta(Long consultaId, Pageable pageable) {
        if (!consultaRepository.existsById(consultaId)) {
            throw new ResourceNotFoundException(ErrorCodes.CONSULTA_NOT_FOUND, ExceptionMessages.notFound("Consulta"));
        }

        Page<ExameSolicitado> page = exameSolicitadoRepository
                .findByConsultaId(consultaId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    // LISTAR EXAMES POR TIPO (EXAME_BASE)
    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorExameBase(Long exameBaseId, Pageable pageable) {
        if (!exameBaseRepository.existsById(exameBaseId)) {
            throw new ResourceNotFoundException(ErrorCodes.EXAME_BASE_NOT_FOUND, ExceptionMessages.notFound("Exame base"));
        }

        Page<ExameSolicitado> page = exameSolicitadoRepository
                .findByExameBaseId(exameBaseId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ErrorCodes.PACIENTE_NOT_FOUND, ExceptionMessages.notFound("Paciente"));
        }

        Page<ExameSolicitado> page =
                exameSolicitadoRepository.findByConsultaPacienteId(pacienteId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorPacienteUltimaConsulta(Long pacienteId,
                                                                       Pageable pageable) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ErrorCodes.PACIENTE_NOT_FOUND, ExceptionMessages.notFound("Paciente"));
        }

        Consulta ultimaConsulta = consultaRepository
                .findTopByPacienteIdOrderByIdDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.CONSULTA_NOT_FOUND,
                        ExceptionMessages.NO_CONSULTATIONS_FOR_PATIENT));

        Page<ExameSolicitado> page =
                exameSolicitadoRepository.findByConsultaId(ultimaConsulta.getId(), pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    private void copyDtoToEntity(ExameSolicitadoUpdateDTO dto, ExameSolicitado entity) {

        entity.setStatus(dto.getStatus());
        entity.setJustificativa(dto.getJustificativa());
        entity.setObservacoes(dto.getObservacoes());
        entity.setDataColeta(dto.getDataColeta());
        entity.setDataResultado(dto.getDataResultado());

        Consulta consulta = consultaRepository.findById(dto.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.CONSULTA_NOT_FOUND, ExceptionMessages.notFound("Consulta")));
        entity.setConsulta(consulta);

        ExameBase exameBase = exameBaseRepository.findById(dto.getExameBaseId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.EXAME_BASE_NOT_FOUND, ExceptionMessages.notFound("Exame base")));
        entity.setExameBase(exameBase);
    }

    private void validatePatch(ExameSolicitado entity, ExameSolicitadoPatchDTO dto) {
        validateStatusTransition(entity, dto);
        validateDateConsistency(entity, dto);
    }

    private void validateStatusTransition(ExameSolicitado entity, ExameSolicitadoPatchDTO dto) {
        if (dto.getStatus() == null || entity.getStatus() == null) {
            return;
        }

        Set<StatusExame> allowedTargets = switch (entity.getStatus()) {
            case SOLICITADO -> EnumSet.of(
                    StatusExame.SOLICITADO,
                    StatusExame.AGENDADO,
                    StatusExame.REALIZADO,
                    StatusExame.CANCELADO
            );
            case AGENDADO -> EnumSet.of(
                    StatusExame.AGENDADO,
                    StatusExame.REALIZADO,
                    StatusExame.CANCELADO
            );
            case REALIZADO -> EnumSet.of(StatusExame.REALIZADO);
            case CANCELADO -> EnumSet.of(StatusExame.CANCELADO);
        };

        if (!allowedTargets.contains(dto.getStatus())) {
            throw new BusinessRuleException(
                    ErrorCodes.EXAME_SOLICITADO_BUSINESS_RULE,
                    "Transição de status de exame não permitida."
            );
        }
    }

    private void validateDateConsistency(ExameSolicitado entity, ExameSolicitadoPatchDTO dto) {
        LocalDateTime dataColeta = dto.getDataColeta() != null ? dto.getDataColeta() : entity.getDataColeta();
        LocalDateTime dataResultado = dto.getDataResultado() != null ? dto.getDataResultado() : entity.getDataResultado();

        if (dataColeta != null && dataResultado != null && dataResultado.isBefore(dataColeta)) {
            throw new BusinessRuleException(
                    ErrorCodes.EXAME_SOLICITADO_BUSINESS_RULE,
                    "A data de resultado não pode ser anterior à data de coleta."
            );
        }
    }

}
