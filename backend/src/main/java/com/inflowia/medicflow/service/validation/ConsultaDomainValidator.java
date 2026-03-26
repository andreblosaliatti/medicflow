package com.inflowia.medicflow.service.validation;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.exception.ExceptionMessages;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ConsultaDomainValidator {

    public void validate(Consulta consulta) {
        validateTeleconsulta(consulta);
        validatePagamento(consulta);
        validatePrescricao(consulta);
    }

    public void validateCanAddMedication(Consulta consulta) {
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED);
        }
    }

    public void validateStatusTransition(StatusConsulta currentStatus, StatusConsulta newStatus) {
        if (currentStatus == null || newStatus == null || currentStatus == newStatus) {
            return;
        }

        if (currentStatus == StatusConsulta.AGENDADA && Set.of(StatusConsulta.CONFIRMADA, StatusConsulta.CANCELADA, StatusConsulta.NAO_COMPARECEU).contains(newStatus)) {
            return;
        }
        if (currentStatus == StatusConsulta.CONFIRMADA && Set.of(StatusConsulta.EM_ATENDIMENTO, StatusConsulta.CANCELADA, StatusConsulta.NAO_COMPARECEU).contains(newStatus)) {
            return;
        }
        if (currentStatus == StatusConsulta.EM_ATENDIMENTO && newStatus == StatusConsulta.CONCLUIDA) {
            return;
        }

        throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.CONSULTATION_STATUS_TRANSITION_NOT_ALLOWED);
    }

    public void validateCanConfirm(Consulta consulta) {
        validateRequiredStatuses(consulta, Set.of(StatusConsulta.AGENDADA), ExceptionMessages.CONSULTATION_CONFIRMATION_NOT_ALLOWED);
    }

    public void validateCanCancel(Consulta consulta) {
        validateRequiredStatuses(consulta, Set.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA), ExceptionMessages.CONSULTATION_CANCELLATION_NOT_ALLOWED);
    }

    public void validateCanStart(Consulta consulta) {
        validateRequiredStatuses(consulta, Set.of(StatusConsulta.CONFIRMADA), ExceptionMessages.CONSULTATION_START_NOT_ALLOWED);
    }

    public void validateCanFinish(Consulta consulta) {
        validateRequiredStatuses(consulta, Set.of(StatusConsulta.EM_ATENDIMENTO), ExceptionMessages.CONSULTATION_FINISH_NOT_ALLOWED);
    }

    private void validateRequiredStatuses(Consulta consulta, Set<StatusConsulta> allowedStatuses, String message) {
        if (consulta.getStatus() == null || !allowedStatuses.contains(consulta.getStatus())) {
            throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, message);
        }
    }

    private void validateTeleconsulta(Consulta consulta) {
        boolean isTeleconsulta = consulta.getTipo() == TipoConsulta.TELECONSULTA;
        if (isTeleconsulta && isBlank(consulta.getLinkAcesso())) {
            throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.TELECONSULTATION_LINK_REQUIRED);
        }
    }

    private void validatePagamento(Consulta consulta) {
        if (Boolean.TRUE.equals(consulta.getPago())) {
            if (consulta.getDataPagamento() == null) {
                throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.PAYMENT_DATE_REQUIRED);
            }

            boolean statusPermitePagamento = consulta.getStatus() == StatusConsulta.CONFIRMADA
                    || consulta.getStatus() == StatusConsulta.EM_ATENDIMENTO
                    || consulta.getStatus() == StatusConsulta.CONCLUIDA;

            if (!statusPermitePagamento) {
                throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.PAID_CONSULTATION_INVALID_STATUS);
            }
        }

        if (!Boolean.TRUE.equals(consulta.getPago()) && consulta.getDataPagamento() != null) {
            throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.PAYMENT_DATE_NOT_ALLOWED);
        }
    }

    private void validatePrescricao(Consulta consulta) {
        if (consulta.getStatus() == StatusConsulta.CANCELADA && !isBlank(consulta.getPrescricao())) {
            throw new BusinessRuleException(ErrorCodes.CONSULTA_BUSINESS_RULE, ExceptionMessages.CANCELED_CONSULTATION_PRESCRIPTION_NOT_ALLOWED);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
