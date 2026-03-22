package com.inflowia.medicflow.dto.medicamento;

import com.inflowia.medicflow.domain.medicamento.MedicamentoPrescrito;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoPrescritoMinDTO {

    private Long id;
    private String nome;
    private String dosagem;
    private String frequencia;
    private String via;
    private Long consultaId;
    private Long pacienteId;
    private Long medicamentoBaseId;
    private String pacienteNome;

    public MedicamentoPrescritoMinDTO(MedicamentoPrescrito m) {
        this.id = m.getId();
        this.nome = m.getNome();
        this.dosagem = m.getDosagem();
        this.frequencia = m.getFrequencia();
        this.via = m.getVia();
        this.consultaId = m.getConsulta() != null ? m.getConsulta().getId() : null;
        this.pacienteId = m.getConsulta() != null && m.getConsulta().getPaciente() != null
                ? m.getConsulta().getPaciente().getId()
                : null;
        this.medicamentoBaseId = m.getMedicamentoBase() != null ? m.getMedicamentoBase().getId() : null;
        this.pacienteNome = m.getConsulta() != null && m.getConsulta().getPaciente() != null
                ? composeName(
                m.getConsulta().getPaciente().getPrimeiroNome(),
                m.getConsulta().getPaciente().getSobrenome())
                : null;
    }

    private String composeName(String first, String second) {
        StringBuilder builder = new StringBuilder();
        if (first != null && !first.isBlank()) {
            builder.append(first.trim());
        }
        if (second != null && !second.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(second.trim());
        }
        return builder.length() == 0 ? null : builder.toString();
    }
}
