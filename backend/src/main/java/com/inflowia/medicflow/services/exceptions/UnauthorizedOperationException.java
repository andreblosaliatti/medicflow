package com.inflowia.medicflow.services.exceptions;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
