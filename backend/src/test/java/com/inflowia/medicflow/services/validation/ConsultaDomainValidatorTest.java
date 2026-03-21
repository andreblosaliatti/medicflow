package com.inflowia.medicflow.services.validation;

import com.inflowia.medicflow.entities.consulta.Consulta;
import com.inflowia.medicflow.entities.consulta.MeioPagamento;
import com.inflowia.medicflow.entities.consulta.StatusConsulta;
import com.inflowia.medicflow.entities.consulta.TipoConsulta;
import com.inflowia.medicflow.services.exceptions.BusinessRuleException;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
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
        consulta.setTeleconsulta(true);
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
    void validateMustAllowPaidConsultaWithSupportedStatus() {
        Consulta consulta = consultaBase();
        consulta.setStatus(StatusConsulta.CONCLUIDA);
        consulta.setPago(true);
        consulta.setDataPagamento(LocalDateTime.now());

        assertDoesNotThrow(() -> validator.validate(consulta));
    }

    private Consulta consultaBase() {
        Consulta consulta = new Consulta();
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setTipo(TipoConsulta.PRESENCIAL);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setMeioPagamento(MeioPagamento.PIX);
        consulta.setPago(false);
        consulta.setTeleconsulta(false);
        consulta.setMotivo("Avaliação clínica");
        return consulta;
    }
}
