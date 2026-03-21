package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.domain.paciente.Paciente;
import com.inflowia.medicflow.domain.usuario.Medico;
import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoComPacientesDTO {

    private Long id;
    private String nome;
    private String sobrenome;
    private String nomeCompleto;
    private String crm;
    private String especialidade;
    private boolean ativo;
    private List<PacienteMinDTO> pacientes;

    public MedicoComPacientesDTO(Medico medico, List<Paciente> pacientesEntity) {
        this.id = medico.getId();
        this.nome = medico.getNome();
        this.sobrenome = medico.getSobrenome();
        this.nomeCompleto = (medico.getNome() + " " + medico.getSobrenome()).trim();
        this.crm = medico.getCrm();
        this.especialidade = medico.getEspecialidade();
        this.ativo = medico.isAtivo();
        this.pacientes = pacientesEntity.stream()
                .map(PacienteMinDTO::new)
                .collect(Collectors.toList());
    }
}
