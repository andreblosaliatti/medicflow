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
import com.inflowia.medicflow.security.CurrentUserScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
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

    @Autowired
    private CurrentUserScope currentUserScope;

    // CREATE
    @Transactional
    public ExameSolicitadoDetailsDTO inserir(ExameSolicitadoUpdateDTO dto) {
        ExameSolicitado entity = new ExameSolicitado();
        copyDtoToEntity(dto, entity);
        entity = exameSolicitadoRepository.save(entity);
        return new ExameSolicitadoDetailsDTO(entity);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        ExameSolicitado entity = getExameSolicitado(id);
        assertCanAccessExameSolicitado(entity);
        try {
            exameSolicitadoRepository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException(ErrorCodes.EXAME_SOLICITADO_BUSINESS_RULE, "Não é possível excluir o exame solicitado informado porque ele está vinculado a outros registros. Utilize a inativação.");
        }
    }

    // FIND BY ID
    @Transactional(readOnly = true)
    public ExameSolicitadoDetailsDTO buscarPorId(Long id) {
        ExameSolicitado entity = getExameSolicitado(id);
        assertCanAccessExameSolicitado(entity);

        return new ExameSolicitadoDetailsDTO(entity);
    }

    @Transactional
    public ExameSolicitadoDetailsDTO atualizar(Long id, ExameSolicitadoPatchDTO dto) {
        ExameSolicitado entity = getExameSolicitado(id);
        assertCanAccessExameSolicitado(entity);

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
        ExameSolicitado entity = getExameSolicitado(id);
        assertCanAccessExameSolicitado(entity);

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
        Consulta consulta = getConsulta(consultaId);
        assertCanAccessConsulta(consulta);

        Page<ExameSolicitado> page = findByConsultaNoEscopo(consultaId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    // LISTAR EXAMES POR TIPO (EXAME_BASE)
    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorExameBase(Long exameBaseId, Pageable pageable) {
        if (!exameBaseRepository.existsById(exameBaseId)) {
            throw new ResourceNotFoundException(ErrorCodes.EXAME_BASE_NOT_FOUND, ExceptionMessages.notFound("Exame base"));
        }

        Page<ExameSolicitado> page = findByExameBaseNoEscopo(exameBaseId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ErrorCodes.PACIENTE_NOT_FOUND, ExceptionMessages.notFound("Paciente"));
        }

        Page<ExameSolicitado> page = findByPacienteNoEscopo(pacienteId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorPacienteUltimaConsulta(Long pacienteId,
                                                                       Pageable pageable) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ErrorCodes.PACIENTE_NOT_FOUND, ExceptionMessages.notFound("Paciente"));
        }

        Consulta ultimaConsulta = findUltimaConsultaPacienteNoEscopo(pacienteId)
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
        assertCanAccessConsulta(consulta);
        entity.setConsulta(consulta);

        ExameBase exameBase = exameBaseRepository.findById(dto.getExameBaseId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.EXAME_BASE_NOT_FOUND, ExceptionMessages.notFound("Exame base")));
        entity.setExameBase(exameBase);
    }

    private ExameSolicitado getExameSolicitado(Long id) {
        return exameSolicitadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.EXAME_SOLICITADO_NOT_FOUND,
                        ExceptionMessages.notFound("Exame solicitado")
                ));
    }

    private Consulta getConsulta(Long consultaId) {
        return consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.CONSULTA_NOT_FOUND,
                        ExceptionMessages.notFound("Consulta")
                ));
    }

    private Page<ExameSolicitado> findByConsultaNoEscopo(Long consultaId, Pageable pageable) {
        if (!currentUserScope.requiresMedicoScope()) {
            return exameSolicitadoRepository.findByConsultaId(consultaId, pageable);
        }

        return exameSolicitadoRepository.findByConsultaIdAndConsultaMedicoId(
                consultaId,
                currentUserScope.requireMedicoId(),
                pageable
        );
    }

    private Page<ExameSolicitado> findByExameBaseNoEscopo(Long exameBaseId, Pageable pageable) {
        if (!currentUserScope.requiresMedicoScope()) {
            return exameSolicitadoRepository.findByExameBaseId(exameBaseId, pageable);
        }

        return exameSolicitadoRepository.findByExameBaseIdAndConsultaMedicoId(
                exameBaseId,
                currentUserScope.requireMedicoId(),
                pageable
        );
    }

    private Page<ExameSolicitado> findByPacienteNoEscopo(Long pacienteId, Pageable pageable) {
        if (!currentUserScope.requiresMedicoScope()) {
            return exameSolicitadoRepository.findByConsultaPacienteId(pacienteId, pageable);
        }

        return exameSolicitadoRepository.findByConsultaPacienteIdAndConsultaMedicoId(
                pacienteId,
                currentUserScope.requireMedicoId(),
                pageable
        );
    }

    private java.util.Optional<Consulta> findUltimaConsultaPacienteNoEscopo(Long pacienteId) {
        if (!currentUserScope.requiresMedicoScope()) {
            return consultaRepository.findTopByPacienteIdOrderByIdDesc(pacienteId);
        }

        return consultaRepository.findTopByPacienteIdAndMedicoIdOrderByIdDesc(
                pacienteId,
                currentUserScope.requireMedicoId()
        );
    }

    private void assertCanAccessExameSolicitado(ExameSolicitado exameSolicitado) {
        assertCanAccessConsulta(exameSolicitado.getConsulta());
    }

    private void assertCanAccessConsulta(Consulta consulta) {
        if (!currentUserScope.requiresMedicoScope()) {
            return;
        }

        Long consultaMedicoId = consulta.getMedico() != null ? consulta.getMedico().getId() : null;
        if (!currentUserScope.requireMedicoId().equals(consultaMedicoId)) {
            throw accessDenied();
        }
    }

    private AccessDeniedException accessDenied() {
        return new AccessDeniedException(ExceptionMessages.ACCESS_DENIED);
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
