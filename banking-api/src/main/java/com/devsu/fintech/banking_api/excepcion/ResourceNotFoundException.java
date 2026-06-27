package com.devsu.fintech.banking_api.excepcion;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BankingException{
    public ResourceNotFoundException(String message) {
        super(message,"RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
