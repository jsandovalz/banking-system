package com.devsu.fintech.banking_api.excepcion;


import org.springframework.http.HttpStatus;

/**
 * Banking base exception class for all related exceptions
 */
public abstract class BankingException extends RuntimeException{

    private final String code;
    private final HttpStatus status;

    protected BankingException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
