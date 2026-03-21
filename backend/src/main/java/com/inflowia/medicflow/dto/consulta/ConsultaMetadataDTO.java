package com.inflowia.medicflow.dto.consulta;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ConsultaMetadataDTO {
    private List<EnumOptionDTO> status;
    private List<EnumOptionDTO> tipos;
    private List<EnumOptionDTO> meiosPagamento;
}
