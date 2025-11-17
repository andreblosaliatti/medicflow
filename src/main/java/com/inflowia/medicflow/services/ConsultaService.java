package com.inflowia.medicflow.services;

import com.inflowia.medicflow.dto.consulta.ConsultaRequestDTO;
import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import com.inflowia.medicflow.repositories.ConsultaRepository;
import com.inflowia.medicflow.repositories.MedicoRepository;
import com.inflowia.medicflow.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoRepository medicoRepository){
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public Consulta criar(ConsultaRequestDTO dto){
        Paciente paciente = pacienteRepository.findById(dto.pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(dto.medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));

        Consulta c = new Consulta(
                dto.dataHora,
                dto.tipo != null ? dto.tipo : TipoConsulta.PRESENCIAL,
                dto.status != null ? dto.status : StatusConsulta.AGENDADA,
                dto.motivo,
                paciente,
                medico
        );
        c.setAnamnese(dto.anamnese);
        c.setExameFisico(dto.exameFisico);
        c.setDiagnostico(dto.diagnostico);
        c.setPrescricao(dto.prescricao);
        c.setObservacoes(dto.observacoes);

        return consultaRepository.save(c);
    }

    public Consulta buscarPorId(Long id){
        return consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada."));
    }

    public List<Consulta> listarTodas(){
        return consultaRepository.findAll();
    }

    @Transactional
    public Consulta atualizar(Long id, ConsultaRequestDTO dto){
        Consulta c =  buscarPorId(id);

        if(dto.dataHora != null) c.setDataHora(dto.dataHora);
        if(dto.tipo != null) c.setTipo(dto.tipo);
        if(dto.status != null) c.setStatus(dto.status);
        if(dto.motivo != null) c.setMotivo(dto.motivo);
        if(dto.anamnese != null) c.setAnamnese(dto.anamnese);
        if(dto.exameFisico != null) c.setExameFisico(dto.exameFisico);
        if(dto.diagnostico != null) c.setDiagnostico(dto.diagnostico);
        if(dto.prescricao != null) c.setPrescricao(dto.prescricao);
        if(dto.observacoes != null) c.setObservacoes(dto.observacoes);

        return consultaRepository.save(c);
    }
    @Transactional
    public void excluir(Long id){
        consultaRepository.deleteById(id);
    }
}
