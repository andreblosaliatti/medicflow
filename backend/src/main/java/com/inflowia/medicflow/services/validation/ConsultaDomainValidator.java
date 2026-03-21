package com.inflowia.medicflow.services.validation;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import com.inflowia.medicflow.services.exceptions.BusinessRuleException;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
import org.springframework.stereotype.Component;

@Component
public class ConsultaDomainValidator {

    public void validate(Consulta consulta) {
        validateTeleconsulta(consulta);
        validatePagamento(consulta);
        validatePrescricao(consulta);
    }

    public void validateCanAddMedication(Consulta consulta) {
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessRuleException(ExceptionMessages.CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED);
        }
    }

    private void validateTeleconsulta(Consulta consulta) {
        boolean isTeleconsulta = consulta.isTeleconsulta() || consulta.getTipo() == TipoConsulta.TELECONSULTA;
        if (isTeleconsulta && isBlank(consulta.getLinkAcesso())) {
            throw new BusinessRuleException(ExceptionMessages.TELECONSULTATION_LINK_REQUIRED);
        }
    }

    private void validatePagamento(Consulta consulta) {
        if (Boolean.TRUE.equals(consulta.getPago())) {
            if (consulta.getDataPagamento() == null) {
                throw new BusinessRuleException(ExceptionMessages.PAYMENT_DATE_REQUIRED);
            }

            boolean statusPermitePagamento = consulta.getStatus() == StatusConsulta.CONFIRMADA
                    || consulta.getStatus() == StatusConsulta.EM_ATENDIMENTO
                    || consulta.getStatus() == StatusConsulta.CONCLUIDA;

            if (!statusPermitePagamento) {
                throw new BusinessRuleException(ExceptionMessages.PAID_CONSULTATION_INVALID_STATUS);
            }
        }

        if (!Boolean.TRUE.equals(consulta.getPago()) && consulta.getDataPagamento() != null) {
            throw new BusinessRuleException(ExceptionMessages.PAYMENT_DATE_NOT_ALLOWED);
        }
    }

    private void validatePrescricao(Consulta consulta) {
        if (consulta.getStatus() == StatusConsulta.CANCELADA && !isBlank(consulta.getPrescricao())) {
            throw new BusinessRuleException(ExceptionMessages.CANCELED_CONSULTATION_PRESCRIPTION_NOT_ALLOWED);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
