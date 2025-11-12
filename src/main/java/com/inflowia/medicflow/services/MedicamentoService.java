package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.medicamento.DadosAtualizacaoMedicamento;
import com.inflowia.medicflow.dto.medicamento.DadosCadastroMedicamento;
import com.inflowia.medicflow.dto.medicamento.DadosListagemMedicamento;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.medicamento.MedicamentoBase;
import com.inflowia.medicflow.entities.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.repositories.ConsultaRepository;

import com.inflowia.medicflow.repositories.MedicamentoBaseRespository;
import com.inflowia.medicflow.repositories.MedicamentoPrescritoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {


    private final ConsultaRepository consultaRepository;
    private final MedicamentoPrescritoRepository medicamentoRepository;
    private final MedicamentoBaseRespository medicamentoBaseRespository;

    public MedicamentoService(ConsultaRepository consultaRepository, MedicamentoPrescritoRepository medicamentoRepository, MedicamentoBaseRespository medicamentoBaseRespository) {
        this.consultaRepository = consultaRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.medicamentoBaseRespository = medicamentoBaseRespository;
    }

    @Transactional(readOnly = true)
    public List<DadosListagemMedicamento> listarMedicamentos(Long consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada."));

        return consulta.getMedicamentoPrescrito()
                .stream()
                .map(DadosListagemMedicamento::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public DadosListagemMedicamento findById(Long id) {
        Optional<MedicamentoPrescrito> obj = medicamentoRepository.findById(id);
        MedicamentoPrescrito entity = obj.orElseThrow(() -> new RuntimeException("Entity not found"));
        return new DadosListagemMedicamento(entity);
    }

    @Transactional(readOnly = true)
    public List<DadosListagemMedicamento> findByPaciente(Long pacienteId) {
        return medicamentoRepository.findByConsultaPacienteId(pacienteId)
                .stream()
                .map(DadosListagemMedicamento::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DadosListagemMedicamento> listarMedicacaoAtual(Long pacienteId) {

        Consulta consulta = consultaRepository
                .findTop1ByPacienteIdOrderByDataHoraDesc(pacienteId);

        if (consulta == null) {
            throw new EntityNotFoundException("Paciente não possui consultas.");
        }

        return consulta.getMedicamentoPrescrito()
                .stream()
                .map(DadosListagemMedicamento :: new)
                .toList();
    }

    @Transactional
    public void removerMedicamento(Long medicamentoId) {
        medicamentoRepository.deleteById(medicamentoId);
    }

    @Transactional
    public void adicionarMedicamento(Long consultaId, DadosCadastroMedicamento dados) {

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        MedicamentoBase base = null;
        if (dados.medicamentoBaseId() != null) {
            base = medicamentoBaseRespository.findById(dados.medicamentoBaseId())
                    .orElseThrow(() -> new RuntimeException("Medicamento base não encontrado"));
        }

        MedicamentoPrescrito prescrito = MedicamentoPrescrito.builder()
                .medicamentoBase(base)
                .nome(dados.nome())
                .dosagem(dados.dosagem())
                .frequencia(dados.frequencia())
                .via(dados.via())
                .consulta(consulta)
                .build();
        consulta.getMedicamentoPrescrito().add(prescrito);
        medicamentoRepository.save(prescrito);
    }

    @Transactional(readOnly = true)
    public List<DadosListagemMedicamento> findAll() {
        List<MedicamentoPrescrito> list = medicamentoRepository.findAll();
        return list.stream().map(x -> new DadosListagemMedicamento(x)).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if (!medicamentoRepository.existsById(id)) {
            throw new ResourceNotFoundEception("Recurso não encontrado");
        }
        try {
            medicamentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}