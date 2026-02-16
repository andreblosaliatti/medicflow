package com.inflowia.medicflow.dto.medico;

import com.inflowia.medicflow.dto.paciente.PacienteMinDTO;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.entities.usuario.Medico;
import lombok.*;

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
    private String crm;
    private String especialidade;
    private boolean ativo;

    // Lista de pacientes atendidos por esse médico
    private List<PacienteMinDTO> pacientes;

    public MedicoComPacientesDTO(Medico medico, List<Paciente> pacientesEntity) {
        this.id = medico.getId();
        this.nome = medico.getNome();
        this.sobrenome = medico.getSobrenome();
        this.crm = medico.getCrm();
        this.especialidade = medico.getEspecialidade();
        this.ativo = medico.isAtivo();

        this.pacientes = pacientesEntity.stream()
                .map(PacienteMinDTO::new)
                .collect(Collectors.toList());
    }
}

