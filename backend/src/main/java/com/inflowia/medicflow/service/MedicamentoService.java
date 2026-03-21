package com.inflowia.medicflow.service;

import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.medicamento.MedicamentoBase;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.MedicamentoBaseRepository;
import com.inflowia.medicflow.repository.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import com.inflowia.medicflow.service.validation.ConsultaDomainValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicamentoService {

    private final ConsultaRepository consultaRepository;
    private final MedicamentoPrescritoRepository medicamentoPrescritoRepository;
    private final MedicamentoBaseRepository medicamentoBaseRepository;
    private final PacienteRepository pacienteRepository;
    private final ConsultaDomainValidator consultaDomainValidator;

    public MedicamentoService(ConsultaRepository consultaRepository,
                              MedicamentoPrescritoRepository medicamentoPrescritoRepository,
                              MedicamentoBaseRepository medicamentoBaseRepository,
                              PacienteRepository pacienteRepository,
                              ConsultaDomainValidator consultaDomainValidator) {
        this.consultaRepository = consultaRepository;
        this.medicamentoPrescritoRepository = medicamentoPrescritoRepository;
        this.medicamentoBaseRepository = medicamentoBaseRepository;
        this.pacienteRepository = pacienteRepository;
        this.consultaDomainValidator = consultaDomainValidator;
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> listarPorConsulta(Long consultaId, Pageable pageable) {
        if (!consultaRepository.existsById(consultaId)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Consulta"));
        }

        Page<MedicamentoPrescrito> page =
                medicamentoPrescritoRepository.findByConsultaId(consultaId, pageable);

        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> listarHistoricoPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Paciente"));
        }

        Page<MedicamentoPrescrito> page =
                medicamentoPrescritoRepository.findByConsultaPacienteId(pacienteId, pageable);

        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public MedicamentoPrescritoMinDTO findById(Long id) {
        Optional<MedicamentoPrescrito> obj = medicamentoPrescritoRepository.findById(id);

        MedicamentoPrescrito entity =
                obj.orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Medicamento")));

        return new MedicamentoPrescritoMinDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> buscaPorNome(String nome, Pageable pageable) {
        Page<MedicamentoPrescrito> page =
                medicamentoPrescritoRepository.searchByName(nome, pageable);

        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public List<MedicamentoPrescritoMinDTO> listarMedicacaoAtual(Long pacienteId) {
        if (!pacienteRepository.existsByIdAndAtivoTrue(pacienteId)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Paciente"));
        }

        Consulta consulta = consultaRepository
                .findTopByPacienteIdOrderByDataHoraDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.NO_PATIENT_CONSULTATIONS));

        return consulta.getMedicamentoPrescrito()
                .stream()
                .map(MedicamentoPrescritoMinDTO::new)
                .toList();
    }

    @Transactional
    public MedicamentoPrescritoMinDTO adicionarMedicamento(Long consultaId, MedicamentoPrescritoDTO dados) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Consulta")));

        consultaDomainValidator.validateCanAddMedication(consulta);

        if (dados.getMedicamentoBaseId() == null &&
                (dados.getNome() == null || dados.getNome().isBlank())) {
            throw new BusinessRuleException(ExceptionMessages.MEDICATION_INFO_REQUIRED);
        }

        if (dados.getDosagem() == null || dados.getDosagem().isBlank()) {
            throw new BusinessRuleException(ExceptionMessages.DOSAGE_REQUIRED);
        }

        if (dados.getFrequencia() == null || dados.getFrequencia().isBlank()) {
            throw new BusinessRuleException(ExceptionMessages.FREQUENCY_REQUIRED);
        }

        if (dados.getVia() == null || dados.getVia().isBlank()) {
            throw new BusinessRuleException(ExceptionMessages.ROUTE_REQUIRED);
        }

        MedicamentoBase base = null;

        if (dados.getMedicamentoBaseId() != null) {
            base = medicamentoBaseRepository.findById(dados.getMedicamentoBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.notFound("Medicamento base")));
        }

        String nome = dados.getNome();

        if (base != null) {
            nome = base.getPrincipioAtivo() != null
                    ? base.getPrincipioAtivo()
                    : base.getNomeComercial();
        }

        MedicamentoPrescrito prescrito = MedicamentoPrescrito.builder()
                .medicamentoBase(base)
                .nome(nome)
                .dosagem(dados.getDosagem())
                .frequencia(dados.getFrequencia())
                .via(dados.getVia())
                .consulta(consulta)
                .build();

        consulta.getMedicamentoPrescrito().add(prescrito);

        MedicamentoPrescrito salvo = medicamentoPrescritoRepository.save(prescrito);

        return new MedicamentoPrescritoMinDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> findAll(Pageable pageable) {
        Page<MedicamentoPrescrito> page =
                medicamentoPrescritoRepository.findAll(pageable);

        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!medicamentoPrescritoRepository.existsById(id)) {
            throw new ResourceNotFoundException(ExceptionMessages.notFound("Medicamento"));
        }

        try {
            medicamentoPrescritoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("Não é possível excluir o medicamento informado porque ele está vinculado a outros registros.");
        }
    }
}
