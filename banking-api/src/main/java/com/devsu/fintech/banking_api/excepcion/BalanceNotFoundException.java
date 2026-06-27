package com.devsu.fintech.banking_api.excepcion;

import org.springframework.http.HttpStatus;

public class BalanceNotFoundException extends BankingException{

    public BalanceNotFoundException(Long accountId) {
        super("Balance not found for account: " + accountId,
                "BALANCE_NOT_FOUND",
                HttpStatus.BAD_REQUEST);
    }
}
