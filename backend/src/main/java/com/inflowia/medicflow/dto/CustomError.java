package com.inflowia.medicflow.dto;

import java.time.Instant;

public class CustomError {

    private final Instant timestamp;
    private final Integer status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;
    private final String traceId;

    public CustomError(Instant timestamp,
                       Integer status,
                       String error,
                       String code,
                       String message,
                       String path,
                       String traceId) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.traceId = traceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getTraceId() {
        return traceId;
    }
}
