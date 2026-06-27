package com.devsu.fintech.banking_api.excepcion;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BankingException{
    public DuplicateResourceException(String message) {
        super(message,"RESOURCE_DUPLICATE", HttpStatus.CONFLICT);
    }
}
