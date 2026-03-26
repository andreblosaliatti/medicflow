package com.inflowia.medicflow.repository.specification;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.dto.consulta.ConsultaFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.JoinType;
import java.util.ArrayList;

public final class ConsultaSpecifications {

    private ConsultaSpecifications() {
    }

    public static Specification<Consulta> withFilters(ConsultaFilterDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            var paciente = root.join("paciente", JoinType.LEFT);
            var medico = root.join("medico", JoinType.LEFT);

            if (filtro.getPacienteId() != null) {
                predicates.add(criteriaBuilder.equal(paciente.get("id"), filtro.getPacienteId()));
            }
            if (filtro.getMedicoId() != null) {
                predicates.add(criteriaBuilder.equal(medico.get("id"), filtro.getMedicoId()));
            }
            if (filtro.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filtro.getStatus()));
            }
            if (filtro.getTipo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), filtro.getTipo()));
            }
            if (filtro.getMeioPagamento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("meioPagamento"), filtro.getMeioPagamento()));
            }
            if (filtro.getPago() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pago"), filtro.getPago()));
            }
            if (filtro.getRetorno() != null) {
                predicates.add(criteriaBuilder.equal(root.get("retorno"), filtro.getRetorno()));
            }
            if (filtro.getDataHoraInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataHora"), filtro.getDataHoraInicio()));
            }
            if (filtro.getDataHoraFim() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataHora"), filtro.getDataHoraFim()));
            }
            if (StringUtils.hasText(filtro.getTermo())) {
                String termo = "%" + filtro.getTermo().trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(paciente.get("nome")), termo),
                        criteriaBuilder.like(criteriaBuilder.lower(paciente.get("sobrenome")), termo),
                        criteriaBuilder.like(criteriaBuilder.lower(medico.get("nome")), termo),
                        criteriaBuilder.like(criteriaBuilder.lower(medico.get("sobrenome")), termo),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("motivo").as(String.class)), termo)
                ));
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
