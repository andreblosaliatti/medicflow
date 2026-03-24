package com.inflowia.medicflow.dto.paciente;

public class PacienteProntuarioMedicacaoDTO {

    private final String nome;
    private final String dose;
    private final String posologia;

    public PacienteProntuarioMedicacaoDTO(String nome, String dose, String posologia) {
        this.nome = nome;
        this.dose = dose;
        this.posologia = posologia;
    }

    public String getNome() {
        return nome;
    }

    public String getDose() {
        return dose;
    }

    public String getPosologia() {
        return posologia;
    }
}
