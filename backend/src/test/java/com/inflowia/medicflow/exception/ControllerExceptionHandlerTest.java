package com.inflowia.medicflow.exception;

import com.inflowia.medicflow.dto.ValidationError;
import com.inflowia.medicflow.exception.BusinessRuleException;
import com.inflowia.medicflow.exception.ExceptionMessages;
import com.inflowia.medicflow.exception.ResourceNotFoundException;
import com.inflowia.medicflow.exception.UnauthorizedOperationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new ControllerExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.setMethod("GET");
        request.setAttribute(ControllerExceptionHandler.TRACE_ID_ATTRIBUTE, "trace-123");
    }

    @Test
    void shouldReturnNotFoundForMissingResource() {
        var response = handler.resourceNotFound(
                new ResourceNotFoundException(ExceptionMessages.notFound("Paciente")),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals(ErrorCodes.RESOURCE_NOT_FOUND, response.getBody().getCode());
        assertEquals("Paciente não encontrado.", response.getBody().getMessage());
        assertEquals("trace-123", response.getBody().getTraceId());
    }

    @Test
    void shouldReturnUnprocessableEntityForBusinessRuleViolation() {
        var response = handler.businessRule(new BusinessRuleException(ExceptionMessages.DOSAGE_REQUIRED), request);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("Unprocessable Entity", response.getBody().getError());
        assertEquals(ErrorCodes.BUSINESS_RULE_VIOLATION, response.getBody().getCode());
        assertEquals(ExceptionMessages.DOSAGE_REQUIRED, response.getBody().getMessage());
    }

    @Test
    void shouldReturnValidationErrorForConstraintViolation() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("patientId");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must not be null");

        var response = handler.constraintViolation(new ConstraintViolationException(Set.of(violation)), request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(ErrorCodes.VALIDATION_ERROR, response.getBody().getCode());
        assertEquals(ExceptionMessages.INVALID_DATA, response.getBody().getMessage());
        ValidationError validationError = (ValidationError) response.getBody();
        assertEquals(1, validationError.getErrors().size());
        assertEquals("patientId", validationError.getErrors().getFirst().getFieldName());
    }

    @Test
    void shouldReturnForbiddenForUnauthorizedOperation() {
        var response = handler.unauthorizedOperation(
                new UnauthorizedOperationException(ExceptionMessages.ACCESS_DENIED),
                request
        );

        assertEquals(403, response.getStatusCode().value());
        assertEquals(ErrorCodes.UNAUTHORIZED_OPERATION, response.getBody().getCode());
        assertEquals(ExceptionMessages.ACCESS_DENIED, response.getBody().getMessage());
    }

    @Test
    void shouldReturnUnauthorizedForAuthenticationFailures() {
        var response = handler.authentication(new BadCredentialsException(ExceptionMessages.INVALID_LOGIN), request);

        assertEquals(401, response.getStatusCode().value());
        assertEquals(ErrorCodes.AUTHENTICATION_ERROR, response.getBody().getCode());
        assertEquals(ExceptionMessages.INVALID_LOGIN, response.getBody().getMessage());
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedExceptions() {
        var response = handler.generic(new RuntimeException("db offline"), request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals(ErrorCodes.INTERNAL_ERROR, response.getBody().getCode());
        assertEquals(ExceptionMessages.INTERNAL_ERROR, response.getBody().getMessage());
        assertEquals("trace-123", response.getBody().getTraceId());
    }
}
