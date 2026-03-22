package com.inflowia.medicflow.exception;

public class ResourceNotFoundException extends RuntimeException{

    private final String code;

    public ResourceNotFoundException(String msg){
        this(ErrorCodes.CONSULTA_NOT_FOUND, msg);
    }

    public ResourceNotFoundException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
