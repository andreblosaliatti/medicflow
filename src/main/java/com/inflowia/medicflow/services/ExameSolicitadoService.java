package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.exame.ExameSolicitadoDetailsDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoMinDTO;
import com.inflowia.medicflow.dto.exame.ExameSolicitadoUpdateDTO;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.exame.ExameBase;
import com.inflowia.medicflow.entities.exame.ExameSolicitado;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.ExameBaseRepository;
import com.inflowia.medicflow.repositories.ExameSolicitadoRepository;
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
            throw new ResourceNotFoundException("Exame solicitado não encontrado");
        }
        try {
            exameSolicitadoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível excluir exame solicitado com exames solicitados. Utilize a inativação (soft delete).");
        }
    }

    // FIND BY ID
    @Transactional(readOnly = true)
    public ExameSolicitadoDetailsDTO buscarPorId(Long id) {
        ExameSolicitado entity = exameSolicitadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exame solicitado não encontrado"));

        return new ExameSolicitadoDetailsDTO(entity);
    }

    // LISTAR EXAMES DE UMA CONSULTA
    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorConsulta(Long consultaId, Pageable pageable) {
        if (!consultaRepository.existsById(consultaId)) {
            throw new ResourceNotFoundException("Consulta não encontrada");
        }

        Page<ExameSolicitado> page = exameSolicitadoRepository
                .findByConsultaId(consultaId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    // LISTAR EXAMES POR TIPO (EXAME_BASE)
    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorExameBase(Long exameBaseId, Pageable pageable) {
        if (!exameBaseRepository.existsById(exameBaseId)) {
            throw new ResourceNotFoundException("Exame base não encontrado");
        }

        Page<ExameSolicitado> page = exameSolicitadoRepository
                .findByExameBaseId(exameBaseId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }

        Page<ExameSolicitado> page =
                exameSolicitadoRepository.findByConsultaPacienteId(pacienteId, pageable);

        return page.map(ExameSolicitadoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExameSolicitadoMinDTO> listarPorPacienteUltimaConsulta(Long pacienteId,
                                                                       Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }

        Consulta ultimaConsulta = consultaRepository
                .findTopByPacienteIdOrderByIdDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nenhuma consulta encontrada para o paciente"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        entity.setConsulta(consulta);

        ExameBase exameBase = exameBaseRepository.findById(dto.getExameBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exame base não encontrado"));
        entity.setExameBase(exameBase);
    }

}
