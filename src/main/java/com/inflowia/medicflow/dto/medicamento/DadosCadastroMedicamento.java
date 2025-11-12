package com.inflowia.medicflow.dto.medicamento;

public record DadosCadastroMedicamento(
        Long medicamentoBaseId,
        String nome,
        String dosagem,
        String frequencia,
        String via)
{}
