package com.inflowia.medicflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inflowia.medicflow.dto.CustomError;
import com.inflowia.medicflow.exception.ControllerExceptionHandler;
import com.inflowia.medicflow.exception.ErrorCodes;
import com.inflowia.medicflow.exception.ExceptionMessages;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ApiAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        CustomError body = new CustomError(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ErrorCodes.AUTH_AUTHENTICATION_ERROR,
                ExceptionMessages.AUTHENTICATION_REQUIRED,
                request.getRequestURI(),
                resolveTraceId(request)
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private String resolveTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute(ControllerExceptionHandler.TRACE_ID_ATTRIBUTE);
        return traceId != null ? traceId.toString() : null;
    }
}