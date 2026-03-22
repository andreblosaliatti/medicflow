package com.inflowia.medicflow.service;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.dto.paciente.PacienteDTO;
import com.inflowia.medicflow.dto.paciente.PacienteHistoricoResumoDTO;
import com.inflowia.medicflow.dto.paciente.PacienteListDTO;
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
        Page<Paciente> page = repository.search(normalizarFiltro(nome), normalizarFiltro(cpf), ativoFilter,
                normalizarFiltro(convenio), pageable);
        return page.map(this::toPacienteListDTO);
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.PACIENTE_NOT_FOUND, ExceptionMessages.notFound("Paciente")));
    }

    private PacienteListDTO toPacienteListDTO(Paciente paciente) {
        return new PacienteListDTO(
                paciente,
                consultaRepository.findTopByPacienteIdOrderByDataHoraDesc(paciente.getId())
                        .map(Consulta::getDataHora)
                        .orElse(null)
        );
    }

    private String normalizarFiltro(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }

    private void copiarDtoParaEntidade(PacienteDTO dto, Paciente entidade) {
        entidade.setPrimeiroNome(dto.getPrimeiroNome());
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
        if (dto.getPrimeiroNome() != null) {
            entidade.setPrimeiroNome(dto.getPrimeiroNome());
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
}
