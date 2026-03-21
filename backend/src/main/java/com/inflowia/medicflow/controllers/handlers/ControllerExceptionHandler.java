package com.inflowia.medicflow.controllers.handlers;

import com.inflowia.medicflow.dto.CustomError;
import com.inflowia.medicflow.dto.ValidationError;
import com.inflowia.medicflow.services.exceptions.BusinessRuleException;
import com.inflowia.medicflow.services.exceptions.ExceptionMessages;
import com.inflowia.medicflow.services.exceptions.ResourceNotFoundException;
import com.inflowia.medicflow.services.exceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.validation.method.ParameterValidationResult;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    public static final String TRACE_ID_ATTRIBUTE = "traceId";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ErrorCodes.RESOURCE_NOT_FOUND, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<CustomError> businessRule(BusinessRuleException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCodes.BUSINESS_RULE_VIOLATION, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation error [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.VALIDATION_ERROR);
        ValidationError err = buildValidationError(HttpStatus.UNPROCESSABLE_ENTITY, ExceptionMessages.INVALID_DATA, request);
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomError> constraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("Constraint violation [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.VALIDATION_ERROR);
        ValidationError err = buildValidationError(HttpStatus.BAD_REQUEST, ExceptionMessages.INVALID_DATA, request);
        e.getConstraintViolations().forEach(violation -> err.addError(violation.getPropertyPath().toString(), violation.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<CustomError> handlerMethodValidation(HandlerMethodValidationException e, HttpServletRequest request) {
        log.warn("Method validation error [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.VALIDATION_ERROR);
        ValidationError err = buildValidationError(HttpStatus.BAD_REQUEST, ExceptionMessages.INVALID_DATA, request);
        for (ParameterValidationResult result : e.getAllValidationResults()) {
            result.getResolvableErrors().forEach(error -> err.addError(result.getMethodParameter().getParameterName(), error.getDefaultMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<CustomError> unauthorizedOperation(UnauthorizedOperationException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ErrorCodes.UNAUTHORIZED_OPERATION, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomError> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_CREDENTIALS, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ErrorCodes.ACCESS_DENIED, ExceptionMessages.ACCESS_DENIED, request, e, false);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomError> authentication(AuthenticationException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCodes.AUTHENTICATION_ERROR, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> generic(Exception e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_ERROR, ExceptionMessages.INTERNAL_ERROR, request, e, true);
    }

    private ResponseEntity<CustomError> buildErrorResponse(HttpStatus status,
                                                           String code,
                                                           String message,
                                                           HttpServletRequest request,
                                                           Exception exception,
                                                           boolean logStackTrace) {
        if (logStackTrace) {
            log.error("Unhandled exception for [{}] {} traceId={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), exception);
        } else {
            log.warn("Handled exception [{}] {} traceId={} code={} message={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), code, exception.getMessage());
        }

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI(),
                resolveTraceId(request)
        );
        return ResponseEntity.status(status).body(err);
    }

    private ValidationError buildValidationError(HttpStatus status, String message, HttpServletRequest request) {
        return new ValidationError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ErrorCodes.VALIDATION_ERROR,
                message,
                request.getRequestURI(),
                resolveTraceId(request)
        );
    }

    private String resolveTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute(TRACE_ID_ATTRIBUTE);
        return traceId != null ? traceId.toString() : null;
    }
}
