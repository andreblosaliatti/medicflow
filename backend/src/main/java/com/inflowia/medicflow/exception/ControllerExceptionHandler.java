package com.inflowia.medicflow.exception;

import com.inflowia.medicflow.dto.CustomError;
import com.inflowia.medicflow.dto.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    public static final String TRACE_ID_ATTRIBUTE = "traceId";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getCode(), e.getMessage(), request, e, false);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<CustomError> businessRule(BusinessRuleException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getCode(), e.getMessage(), request, e, false);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation error [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.CORE_REQUEST_BODY_INVALID);
        ValidationError err = buildValidationError(HttpStatus.UNPROCESSABLE_ENTITY, ExceptionMessages.INVALID_DATA, request);
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomError> constraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("Constraint violation [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.CORE_REQUEST_INVALID);
        ValidationError err = buildValidationError(HttpStatus.BAD_REQUEST, ExceptionMessages.INVALID_DATA, request);
        e.getConstraintViolations().forEach(violation -> err.addError(violation.getPropertyPath().toString(), violation.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class, ConversionFailedException.class})
    public ResponseEntity<CustomError> invalidRequestFormat(Exception e, HttpServletRequest request) {
        log.warn("Request format error [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.CORE_REQUEST_INVALID);
        ValidationError err = buildValidationError(HttpStatus.BAD_REQUEST, ExceptionMessages.INVALID_DATA, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<CustomError> handlerMethodValidation(HandlerMethodValidationException e, HttpServletRequest request) {
        log.warn("Method validation error [{}] {} traceId={} code={}", request.getMethod(), request.getRequestURI(), resolveTraceId(request), ErrorCodes.CORE_REQUEST_INVALID);
        ValidationError err = buildValidationError(HttpStatus.BAD_REQUEST, ExceptionMessages.INVALID_DATA, request);
        for (ParameterValidationResult result : e.getAllValidationResults()) {
            result.getResolvableErrors().forEach(error -> err.addError(result.getMethodParameter().getParameterName(), error.getDefaultMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<CustomError> unauthorizedOperation(UnauthorizedOperationException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getCode(), e.getMessage(), request, e, false);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomError> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCodes.AUTH_INVALID_CREDENTIALS, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ErrorCodes.AUTH_ACCESS_DENIED, ExceptionMessages.ACCESS_DENIED, request, e, false);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomError> authentication(AuthenticationException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorCodes.AUTH_AUTHENTICATION_ERROR, e.getMessage(), request, e, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> generic(Exception e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.CORE_INTERNAL_ERROR, ExceptionMessages.INTERNAL_ERROR, request, e, true);
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
                status == HttpStatus.UNPROCESSABLE_ENTITY ? ErrorCodes.CORE_REQUEST_BODY_INVALID : ErrorCodes.CORE_REQUEST_INVALID,
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
