package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.exame.ExameSolicitado;
import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteHistoricoResumoDTO;
import com.inflowia.medicflow.dto.paciente.PacienteListDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProntuarioConsultaDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProntuarioDetailsDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProntuarioDiagnosticoDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProntuarioExameDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProntuarioMedicacaoDTO;
import com.inflowia.medicflow.dto.paciente.PacienteProfileDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUltimaConsultaResumoDTO;
import com.inflowia.medicflow.dto.paciente.PacienteUpdateDTO;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import com.inflowia.medicflow.repository.ConsultaRepository;
import com.inflowia.medicflow.repository.ExameSolicitadoRepository;
import com.inflowia.medicflow.repository.MedicamentoPrescritoRepository;
import com.inflowia.medicflow.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ExameSolicitadoRepository exameSolicitadoRepository;

    @Autowired
    private MedicamentoPrescritoRepository medicamentoPrescritoRepository;

    @Transactional
    public PacienteDTO cadastrar(PacienteDTO dto) {
        Paciente entidade = new Paciente();
        copiarDtoParaEntidade(dto, entidade);
        entidade.setAtivo(true);

        Paciente salvo = repository.save(entidade);
        return new PacienteDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<PacienteListDTO> listar(String nome,
                                        String cpf,
                                        Boolean ativo,
                                        String convenio,
                                        Pageable pageable) {
        Boolean ativoFilter = ativo != null ? ativo : Boolean.TRUE;

        Page<Paciente> page = repository.search(
                normalizarFiltro(nome),
                normalizarFiltro(cpf),
                ativoFilter,
                normalizarFiltro(convenio),
                pageable
        );

        List<Paciente> pacientes = page.getContent();
        Map<Long, LocalDateTime> ultimasConsultasPorPaciente = buscarUltimasConsultasPorPaciente(pacientes);

        return page.map(paciente -> new PacienteListDTO(
                paciente,
                ultimasConsultasPorPaciente.get(paciente.getId())
        ));
    }

    @Transactional(readOnly = true)
    public Page<PacienteListDTO> listarInativos(String nome,
                                                String cpf,
                                                String convenio,
                                                Pageable pageable) {
        return listar(nome, cpf, Boolean.FALSE, convenio, pageable);
    }

    @Transactional(readOnly = true)
    public PacienteDTO buscarPorId(Long id) {
        Paciente paciente = getPacienteAtivo(id);
        return new PacienteDTO(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteProfileDTO buscarPerfil(Long id) {
        Paciente paciente = getPacienteAtivo(id);
        Consulta ultimaConsulta = consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(id).orElse(null);

        PacienteUltimaConsultaResumoDTO ultimaConsultaResumo = ultimaConsulta != null
                ? new PacienteUltimaConsultaResumoDTO(ultimaConsulta)
                : null;

        PacienteHistoricoResumoDTO historico = new PacienteHistoricoResumoDTO(
                consultaRepository.countByPacienteId(id),
                exameSolicitadoRepository.countByConsultaPacienteId(id),
                medicamentoPrescritoRepository.countByConsultaPacienteId(id),
                ultimaConsulta != null ? ultimaConsulta.getDataHora() : null,
                ultimaConsultaResumo
        );

        return new PacienteProfileDTO(paciente, historico);
    }

    @Transactional(readOnly = true)
    public PacienteProntuarioDetailsDTO buscarProntuario(Long id) {
        Paciente paciente = getPacienteAtivo(id);
        List<Consulta> consultas = consultaRepository.findByPacienteIdOrderByDataHoraDesc(id);
        List<MedicamentoPrescrito> medicamentos = medicamentoPrescritoRepository.findByConsultaPacienteId(id);
        List<ExameSolicitado> exames = exameSolicitadoRepository.findByConsultaPacienteId(id);

        Map<Long, List<PacienteProntuarioMedicacaoDTO>> medicacoesPorConsulta = medicamentos.stream()
                .collect(Collectors.groupingBy(
                        med -> med.getConsulta().getId(),
                        Collectors.mapping(this::toProntuarioMedicacao, Collectors.toList())
                ));

        Map<Long, List<PacienteProntuarioExameDTO>> examesPorConsulta = exames.stream()
                .collect(Collectors.groupingBy(
                        exame -> exame.getConsulta().getId(),
                        Collectors.mapping(this::toProntuarioExame, Collectors.toList())
                ));

        List<PacienteProntuarioConsultaDTO> consultasDto = consultas.stream()
                .map(consulta -> new PacienteProntuarioConsultaDTO(
                        consulta.getId(),
                        consulta.getDataHora(),
                        composeNomeMedico(consulta),
                        consulta.getMotivo(),
                        consulta.getAnamnese(),
                        consulta.getExameFisico(),
                        consulta.getObservacoes(),
                        toDiagnosticos(consulta.getDiagnostico()),
                        medicacoesPorConsulta.getOrDefault(consulta.getId(), List.of()),
                        examesPorConsulta.getOrDefault(consulta.getId(), List.of())
                ))
                .toList();

        return new PacienteProntuarioDetailsDTO(
                paciente.getId(),
                composeNomePaciente(paciente),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                consultasDto
        );
    }

    @Transactional
    public PacienteDTO atualizar(Long id, PacienteUpdateDTO dto) {
        Paciente paciente = getPacienteAtivo(id);
        copiarUpdateDtoParaEntidade(dto, paciente);

        Paciente atualizado = repository.save(paciente);
        return new PacienteDTO(atualizado);
    }

    @Transactional
    public void delete(Long id) {
        Paciente paciente = getPacienteAtivo(id);
        paciente.setAtivo(false);
        repository.save(paciente);
    }

    @Transactional
    public void softDelete(Long id) {
        delete(id);
    }

    private Paciente getPacienteAtivo(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.PACIENTE_NOT_FOUND,
                        ExceptionMessages.notFound("Paciente")
                ));
    }

    private Map<Long, LocalDateTime> buscarUltimasConsultasPorPaciente(List<Paciente> pacientes) {
        if (pacientes == null || pacientes.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> pacienteIds = pacientes.stream()
                .map(Paciente::getId)
                .toList();

        List<Consulta> consultas = consultaRepository.buscarConsultasOrdenadasPorPacienteEDataDesc(pacienteIds);

        Map<Long, LocalDateTime> mapa = new HashMap<>();

        for (Consulta consulta : consultas) {
            Long pacienteId = consulta.getPaciente().getId();
            mapa.putIfAbsent(pacienteId, consulta.getDataHora());
        }

        return mapa;
    }

    private String normalizarFiltro(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }

    private void copiarDtoParaEntidade(PacienteDTO dto, Paciente entidade) {
        entidade.setNome(dto.getNome());
        entidade.setSobrenome(dto.getSobrenome());
        entidade.setCpf(dto.getCpf());
        entidade.setDataNascimento(dto.getDataNascimento());
        entidade.setTelefone(dto.getTelefone());
        entidade.setEmail(dto.getEmail());
        entidade.setSexo(dto.getSexo());
        entidade.setPlanoSaude(dto.getPlanoSaude());

        if (dto.getEndereco() != null) {
            entidade.setEndereco(dto.getEndereco().toEntity());
        }
    }

    private void copiarUpdateDtoParaEntidade(PacienteUpdateDTO dto, Paciente entidade) {
        if (dto.getNome() != null) {
            entidade.setNome(dto.getNome());
        }

        if (dto.getSobrenome() != null) {
            entidade.setSobrenome(dto.getSobrenome());
        }

        if (dto.getTelefone() != null) {
            entidade.setTelefone(dto.getTelefone());
        }

        if (dto.getEmail() != null) {
            entidade.setEmail(dto.getEmail());
        }

        if (dto.getSexo() != null) {
            entidade.setSexo(dto.getSexo());
        }

        if (dto.getDataNascimento() != null) {
            entidade.setDataNascimento(dto.getDataNascimento());
        }

        if (dto.getPlanoSaude() != null) {
            entidade.setPlanoSaude(dto.getPlanoSaude());
        }

        if (dto.getEndereco() != null) {
            entidade.setEndereco(dto.getEndereco().toEntity());
        }
    }

    private List<PacienteProntuarioDiagnosticoDTO> toDiagnosticos(String diagnostico) {
        if (!StringUtils.hasText(diagnostico)) {
            return List.of();
        }
        return List.of(new PacienteProntuarioDiagnosticoDTO(diagnostico.trim()));
    }

    private PacienteProntuarioMedicacaoDTO toProntuarioMedicacao(MedicamentoPrescrito medicamento) {
        return new PacienteProntuarioMedicacaoDTO(
                medicamento.getNome(),
                medicamento.getDosagem(),
                composePosologia(medicamento.getFrequencia(), medicamento.getVia())
        );
    }

    private PacienteProntuarioExameDTO toProntuarioExame(ExameSolicitado exame) {
        String nomeExame = exame.getExameBase() != null ? exame.getExameBase().getNome() : null;
        return new PacienteProntuarioExameDTO(nomeExame, exame.getStatus());
    }

    private String composePosologia(String frequencia, String via) {
        String freqNormalizada = StringUtils.hasText(frequencia) ? frequencia.trim() : null;
        String viaNormalizada = StringUtils.hasText(via) ? via.trim() : null;

        if (freqNormalizada == null && viaNormalizada == null) {
            return null;
        }
        if (freqNormalizada == null) {
            return viaNormalizada;
        }
        if (viaNormalizada == null) {
            return freqNormalizada;
        }
        return freqNormalizada + " • " + viaNormalizada;
    }

    private String composeNomePaciente(Paciente paciente) {
        String nome = paciente.getNome() != null ? paciente.getNome().trim() : "";
        String sobrenome = paciente.getSobrenome() != null ? paciente.getSobrenome().trim() : "";
        return (nome + " " + sobrenome).trim();
    }

    private String composeNomeMedico(Consulta consulta) {
        if (consulta.getMedico() == null) {
            return null;
        }
        String nome = consulta.getMedico().getNome() != null ? consulta.getMedico().getNome().trim() : "";
        String sobrenome = consulta.getMedico().getSobrenome() != null ? consulta.getMedico().getSobrenome().trim() : "";
        return (nome + " " + sobrenome).trim();
    }
}
