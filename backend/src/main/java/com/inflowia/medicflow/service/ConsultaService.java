package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.dto.consulta.ConsultaAgendaItemDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaDetailsDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaFilterDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaMetadataDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaMinDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaTableItemDTO;
import com.inflowia.medicflow.dto.consulta.ConsultaUpdateDTO;
import com.inflowia.medicflow.dto.consulta.EnumOptionDTO;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.repository.specification.ConsultaSpecifications;
import com.inflowia.medicflow.service.validation.ConsultaDomainValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaDomainValidator consultaDomainValidator;

    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoRepository medicoRepository,
                           ConsultaDomainValidator consultaDomainValidator) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaDomainValidator = consultaDomainValidator;
    }

    @Transactional
    public ConsultaDetailsDTO criar(ConsultaDTO dto) {
        Paciente paciente = pacienteRepository.findByIdAndAtivoTrue(dto.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Paciente")));

        Medico medico = medicoRepository.findByIdAndAtivoTrue(dto.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Médico")));

        Consulta entity = new Consulta();
        copyCreateDtoToEntity(dto, entity, paciente, medico);
        consultaDomainValidator.validate(entity);

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
                paciente = pacienteRepository.findByIdAndAtivoTrue(dto.getPacienteId())
                        .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Paciente")));
            }

            if (dto.getMedicoId() != null) {
                medico = medicoRepository.findByIdAndAtivoTrue(dto.getMedicoId())
                        .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Médico")));
            }

            if (dto.getStatus() != null) {
                consultaDomainValidator.validateStatusTransition(entity.getStatus(), dto.getStatus());
            }

            copyUpdateDtoToEntity(dto, entity, paciente, medico);
            consultaDomainValidator.validate(entity);

            entity = consultaRepository.save(entity);
            return new ConsultaDetailsDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Consulta"));
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!consultaRepository.existsById(id)){
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Consulta"));
        }
        try {
            consultaRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("Não é possível excluir a consulta informada porque ela está vinculada a outros registros.");
        }
    }

    @Transactional(readOnly = true)
    public ConsultaDetailsDTO buscarPorId(Long id) {
        Consulta entity = getConsulta(id);
        return new ConsultaDetailsDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listar(ConsultaFilterDTO filtro, Pageable pageable) {
        ConsultaFilterDTO filtroNormalizado = normalizarFiltro(filtro);
        validarFiltros(filtroNormalizado);
        Page<Consulta> page = consultaRepository.findAll(ConsultaSpecifications.withFilters(filtroNormalizado), pageable);
        return page.map(ConsultaMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaTableItemDTO> listarParaTabela(ConsultaFilterDTO filtro, Pageable pageable) {
        ConsultaFilterDTO filtroNormalizado = normalizarFiltro(filtro);
        validarFiltros(filtroNormalizado);
        Page<Consulta> page = consultaRepository.findAll(ConsultaSpecifications.withFilters(filtroNormalizado), pageable);
        return page.map(ConsultaTableItemDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaAgendaItemDTO> listarParaAgenda(ConsultaFilterDTO filtro, Pageable pageable) {
        ConsultaFilterDTO filtroNormalizado = normalizarFiltro(filtro);
        validarFiltros(filtroNormalizado);
        Page<Consulta> page = consultaRepository.findAll(ConsultaSpecifications.withFilters(filtroNormalizado), pageable);
        return page.map(ConsultaAgendaItemDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Paciente"));
        }
        return listar(ConsultaFilterDTO.builder().pacienteId(pacienteId).build(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listarPorMedico(Long medicoId, Pageable pageable) {
        if (!medicoRepository.existsByIdAndAtivoTrue(medicoId)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Médico"));
        }
        return listar(ConsultaFilterDTO.builder().medicoId(medicoId).build(), pageable);
    }

    @Transactional(readOnly = true)
    public ConsultaDetailsDTO buscarUltimaConsultaPorPaciente(Long pacienteId) {
        Consulta consulta = consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.NO_CONSULTATIONS_FOR_PATIENT));
        return new ConsultaDetailsDTO(consulta);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listarPorStatus(StatusConsulta status, Pageable pageable) {
        return listar(ConsultaFilterDTO.builder().status(status).build(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaMinDTO> listarPorPeriodo(ConsultaFilterDTO filtro, Pageable pageable) {
        return listar(filtro, pageable);
    }

    @Transactional(readOnly = true)
    public ConsultaMetadataDTO listarMetadata() {
        return new ConsultaMetadataDTO(
                toOptions(StatusConsulta.values()),
                toOptions(TipoConsulta.values()),
                toOptions(MeioPagamento.values())
        );
    }

    @Transactional
    public ConsultaDetailsDTO confirmar(Long id) {
        Consulta consulta = getConsulta(id);
        consultaDomainValidator.validateCanConfirm(consulta);
        consultaDomainValidator.validateStatusTransition(consulta.getStatus(), StatusConsulta.CONFIRMADA);
        consulta.setStatus(StatusConsulta.CONFIRMADA);
        consultaDomainValidator.validate(consulta);
        return new ConsultaDetailsDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaDetailsDTO cancelar(Long id) {
        Consulta consulta = getConsulta(id);
        consultaDomainValidator.validateCanCancel(consulta);
        consultaDomainValidator.validateStatusTransition(consulta.getStatus(), StatusConsulta.CANCELADA);
        consulta.setStatus(StatusConsulta.CANCELADA);
        consultaDomainValidator.validate(consulta);
        return new ConsultaDetailsDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaDetailsDTO iniciarAtendimento(Long id) {
        Consulta consulta = getConsulta(id);
        consultaDomainValidator.validateCanStart(consulta);
        consultaDomainValidator.validateStatusTransition(consulta.getStatus(), StatusConsulta.EM_ATENDIMENTO);
        consulta.setStatus(StatusConsulta.EM_ATENDIMENTO);
        consultaDomainValidator.validate(consulta);
        return new ConsultaDetailsDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaDetailsDTO finalizarAtendimento(Long id) {
        Consulta consulta = getConsulta(id);
        consultaDomainValidator.validateCanFinish(consulta);
        consultaDomainValidator.validateStatusTransition(consulta.getStatus(), StatusConsulta.CONCLUIDA);
        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consultaDomainValidator.validate(consulta);
        return new ConsultaDetailsDTO(consultaRepository.save(consulta));
    }

    private Consulta getConsulta(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Consulta")));
    }

    private List<EnumOptionDTO> toOptions(StatusConsulta[] values) {
        return Arrays.stream(values)
                .map(item -> new EnumOptionDTO(item.getCode(), item.getLabel()))
                .toList();
    }

    private List<EnumOptionDTO> toOptions(TipoConsulta[] values) {
        return Arrays.stream(values)
                .map(item -> new EnumOptionDTO(item.getCode(), item.getLabel()))
                .toList();
    }

    private List<EnumOptionDTO> toOptions(MeioPagamento[] values) {
        return Arrays.stream(values)
                .map(item -> new EnumOptionDTO(item.getCode(), item.getLabel()))
                .toList();
    }

    private ConsultaFilterDTO normalizarFiltro(ConsultaFilterDTO filtro) {
        return filtro != null ? filtro : new ConsultaFilterDTO();
    }

    private void validarFiltros(ConsultaFilterDTO filtro) {
        if (filtro == null) {
            return;
        }

        if (filtro.getPacienteId() != null && !pacienteRepository.existsByIdAndAtivoTrue(filtro.getPacienteId())) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Paciente"));
        }

        if (filtro.getMedicoId() != null && !medicoRepository.existsByIdAndAtivoTrue(filtro.getMedicoId())) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Médico"));
        }

        if (filtro.getDataHoraInicio() != null && filtro.getDataHoraFim() != null
                && filtro.getDataHoraInicio().isAfter(filtro.getDataHoraFim())) {
            throw new BusinessRuleException(ExceptionMessages.INVALID_CONSULTATION_PERIOD);
        }

        if (StringUtils.hasText(filtro.getTermo())) {
            filtro.setTermo(filtro.getTermo().trim());
        }
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
