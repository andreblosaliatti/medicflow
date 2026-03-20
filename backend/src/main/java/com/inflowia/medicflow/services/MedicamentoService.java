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
import com.inflowia.medicflow.services.exceptions.DatabaseException;
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
            throw new ResourceNotFoundException("Consulta não encontrada");
        }

        Page<MedicamentoPrescrito> page =
                medicamentoPrescritoRepository.findAllByConsultaId(consultaId, pageable);

        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> listarHistoricoPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }

        Page<MedicamentoPrescrito> page =
                medicamentoPrescritoRepository.findAllByConsultaPacienteId(pacienteId, pageable);

        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public MedicamentoPrescritoMinDTO findById(Long id) {
        Optional<MedicamentoPrescrito> obj = medicamentoPrescritoRepository.findById(id);

        MedicamentoPrescrito entity =
                obj.orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

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
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }

        Consulta consulta = consultaRepository
                .findTopByPacienteIdOrderByDataHoraDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não possui consultas"));

        return consulta.getMedicamentoPrescrito()
                .stream()
                .map(MedicamentoPrescritoMinDTO::new)
                .toList();
    }

    @Transactional
    public MedicamentoPrescritoMinDTO adicionarMedicamento(Long consultaId, MedicamentoPrescritoDTO dados) {

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        if (dados.getMedicamentoBaseId() == null &&
                (dados.getNome() == null || dados.getNome().isBlank())) {
            throw new IllegalArgumentException("Informe o medicamento (nome ou base)");
        }

        if (dados.getDosagem() == null || dados.getDosagem().isBlank()) {
            throw new IllegalArgumentException("Dosagem é obrigatória");
        }

        if (dados.getFrequencia() == null || dados.getFrequencia().isBlank()) {
            throw new IllegalArgumentException("Frequência é obrigatória");
        }

        if (dados.getVia() == null || dados.getVia().isBlank()) {
            throw new IllegalArgumentException("Via é obrigatória");
        }

        MedicamentoBase base = null;

        if (dados.getMedicamentoBaseId() != null) {
            base = medicamentoBaseRepository.findById(dados.getMedicamentoBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicamento base não encontrado"));
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

        // mantém consistência da relação
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
            throw new ResourceNotFoundException("Medicamento não encontrado");
        }

        try {
            medicamentoPrescritoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}