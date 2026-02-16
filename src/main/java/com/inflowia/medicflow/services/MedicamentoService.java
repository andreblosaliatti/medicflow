package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoDTO;
import com.inflowia.medicflow.dto.medicamento.MedicamentoPrescritoMinDTO;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.medicamento.MedicamentoBase;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.repositories.ConsultaRepository;

import com.inflowia.medicflow.repositories.MedicamentoBaseRespository;
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
    private MedicamentoPrescritoRepository medicamentoRepository;

    @Autowired
    private MedicamentoBaseRespository medicamentoBaseRespository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> listarMedicamentos(Long consultaId, Pageable pageable) {
        if (!consultaRepository.existsById(consultaId)) {
            throw new ResourceNotFoundException("Consulta não encontrada");
        }
        Page<MedicamentoPrescrito> page = medicamentoRepository.findByConsultaPacienteId(consultaId, pageable);
        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public MedicamentoPrescritoMinDTO findById(Long id) {
        Optional<MedicamentoPrescrito> obj = medicamentoRepository.findById(id);
        MedicamentoPrescrito entity = obj.orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        return new MedicamentoPrescritoMinDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> findByPaciente(Long pacienteId,  Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }
        Page<MedicamentoPrescrito> page = medicamentoRepository.findByConsultaPacienteId(pacienteId, pageable);
        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> buscaPorNome(String nome, Pageable pageable) {
        Page<MedicamentoPrescrito> medicamento = medicamentoRepository.searchByName(nome, pageable);
        return medicamento.map(x -> new MedicamentoPrescritoMinDTO(x));
    }

    @Transactional(readOnly = true)
    public List<MedicamentoPrescritoMinDTO> listarMedicacaoAtual(Long pacienteId) {

        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }

        Consulta consulta = consultaRepository
                .findTopByPacienteIdOrderByDataHoraDesc(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não possui consultas."));

        return consulta.getMedicamentoPrescrito()
                .stream()
                .map(MedicamentoPrescritoMinDTO::new)
                .toList();
    }

    @Transactional
    public MedicamentoPrescritoMinDTO adicionarMedicamento(Long consultaId, MedicamentoPrescritoDTO dados) {

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        MedicamentoBase base = null;
        if (dados.getMedicamentoBaseId() != null) {
            base = medicamentoBaseRespository.findById(dados.getMedicamentoBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicamento base não encontrado"));
        }

        MedicamentoPrescrito prescrito = MedicamentoPrescrito.builder()
                .medicamentoBase(base)
                .nome(dados.getNome())
                .dosagem(dados.getDosagem())
                .frequencia(dados.getFrequencia())
                .via(dados.getVia())
                .consulta(consulta)
                .build();
        consulta.getMedicamentoPrescrito().add(prescrito);
        MedicamentoPrescrito salvo = medicamentoRepository.save(prescrito);

        return new MedicamentoPrescritoMinDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<MedicamentoPrescritoMinDTO> findAll(Pageable pageable) {
        Page<MedicamentoPrescrito> page = medicamentoRepository.findAll(pageable);
        return page.map(MedicamentoPrescritoMinDTO::new);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if (!medicamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            medicamentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}