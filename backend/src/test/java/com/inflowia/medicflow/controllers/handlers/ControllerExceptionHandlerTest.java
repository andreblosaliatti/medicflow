package com.inflowia.medicflow.controllers.handlers;

import com.inflowia.medicflow.services.exceptions.BusinessRuleException;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import com.inflowia.medicflow.services.exceptions.UnauthorizedOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new ControllerExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void shouldReturnNotFoundForMissingResource() {
        var response = handler.resourceNotFound(
                new ResourceNotFoundException(ExceptionMessages.notFound("Paciente")),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Paciente não encontrado.", response.getBody().getError());
    }

    @Test
    void shouldReturnUnprocessableEntityForBusinessRuleViolation() {
        var response = handler.businessRule(new BusinessRuleException(ExceptionMessages.DOSAGE_REQUIRED), request);

        assertEquals(422, response.getStatusCode().value());
        assertEquals(ExceptionMessages.DOSAGE_REQUIRED, response.getBody().getError());
    }

    @Test
    void shouldReturnForbiddenForUnauthorizedOperation() {
        var response = handler.unauthorizedOperation(
                new UnauthorizedOperationException(ExceptionMessages.ACCESS_DENIED),
                request
        );

        assertEquals(403, response.getStatusCode().value());
        assertEquals(ExceptionMessages.ACCESS_DENIED, response.getBody().getError());
    }

    @Test
    void shouldReturnUnauthorizedForAuthenticationFailures() {
        var response = handler.authentication(new BadCredentialsException(ExceptionMessages.INVALID_LOGIN), request);

        assertEquals(401, response.getStatusCode().value());
        assertEquals(ExceptionMessages.INVALID_LOGIN, response.getBody().getError());
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedExceptions() {
        var response = handler.generic(new RuntimeException("db offline"), request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals(ExceptionMessages.INTERNAL_ERROR, response.getBody().getError());
    }
}
