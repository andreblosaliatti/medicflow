package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.medicamento.MedicamentoBase;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicamentoBaseRepository;
import com.inflowia.medicflow.repositories.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repositories.PacienteRepository;
import com.inflowia.medicflow.services.exceptions.BusinessRuleException;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicamentoPrescritoRepository medicamentoPrescritoRepository;

    @Autowired
    private MedicamentoBaseRepository medicamentoBaseRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

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