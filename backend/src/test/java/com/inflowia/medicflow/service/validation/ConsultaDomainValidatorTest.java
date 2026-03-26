package com.inflowia.medicflow.service.validation;

import com.inflowia.medicflow.domain.consulta.Consulta;
import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultaDomainValidatorTest {

    private final ConsultaDomainValidator validator = new ConsultaDomainValidator();

    @Test
    void validateMustRequireLinkForTeleconsulta() {
        Consulta consulta = consultaBase();
        consulta.setTipo(TipoConsulta.TELECONSULTA);
        consulta.setLinkAcesso("   ");

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> validator.validate(consulta));

        assertEquals(ExceptionMessages.TELECONSULTATION_LINK_REQUIRED, exception.getMessage());
    }

    @Test
    void validateMustRequirePaymentDateWhenPaid() {
        Consulta consulta = consultaBase();
        consulta.setStatus(StatusConsulta.CONFIRMADA);
        consulta.setPago(true);
        consulta.setDataPagamento(null);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> validator.validate(consulta));

        assertEquals(ExceptionMessages.PAYMENT_DATE_REQUIRED, exception.getMessage());
    }

    @Test
    void validateMustRejectPrescriptionForCanceledConsulta() {
        Consulta consulta = consultaBase();
        consulta.setStatus(StatusConsulta.CANCELADA);
        consulta.setPrescricao("Repouso e hidratação");

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> validator.validate(consulta));

        assertEquals(ExceptionMessages.CANCELED_CONSULTATION_PRESCRIPTION_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void validateCanAddMedicationMustRejectCanceledConsulta() {
        Consulta consulta = consultaBase();
        consulta.setStatus(StatusConsulta.CANCELADA);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> validator.validateCanAddMedication(consulta));

        assertEquals(ExceptionMessages.CANCELED_CONSULTATION_MEDICATION_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void validateMustRejectPaidConsultaWithUnsupportedStatus() {
        Consulta consulta = consultaBase();
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setPago(true);
        consulta.setDataPagamento(LocalDateTime.now());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> validator.validate(consulta));

        assertEquals(ExceptionMessages.PAID_CONSULTATION_INVALID_STATUS, exception.getMessage());
    }

    @Test
    void validateMustAllowPaidConsultaWithSupportedStatus() {
        Consulta consulta = consultaBase();
        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consulta.setPago(true);
        consulta.setDataPagamento(LocalDateTime.now());

        assertDoesNotThrow(() -> validator.validate(consulta));
    }

    @Test
    void validateStatusTransitionMustAllowLegacyMappedFlow() {
        assertDoesNotThrow(() -> validator.validateStatusTransition(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA));
        assertDoesNotThrow(() -> validator.validateStatusTransition(StatusConsulta.CONFIRMADA, StatusConsulta.EM_ATENDIMENTO));
        assertDoesNotThrow(() -> validator.validateStatusTransition(StatusConsulta.EM_ATENDIMENTO, StatusConsulta.CONCLUIDA));
    }

    @Test
    void validateStatusTransitionMustRejectSkipToConcluded() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> validator.validateStatusTransition(StatusConsulta.AGENDADA, StatusConsulta.CONCLUIDA));

        assertEquals(ExceptionMessages.CONSULTATION_STATUS_TRANSITION_NOT_ALLOWED, exception.getMessage());
    }

    private Consulta consultaBase() {
        Consulta consulta = new Consulta();
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPago(false);
        consulta.setMotivo("Avaliação clínica");
        return consulta;
    }
}