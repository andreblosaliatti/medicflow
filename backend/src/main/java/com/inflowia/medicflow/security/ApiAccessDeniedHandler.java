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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ApiAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        CustomError body = new CustomError(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ErrorCodes.ACCESS_DENIED,
                ExceptionMessages.ACCESS_DENIED,
                request.getRequestURI(),
                resolveTraceId(request)
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private String resolveTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute(ControllerExceptionHandler.TRACE_ID_ATTRIBUTE);
        return traceId != null ? traceId.toString() : null;
    }
}
