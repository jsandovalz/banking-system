package com.devsu.fintech.banking_api.excepcion;

import org.springframework.http.HttpStatus;

public class DailyLimitExceededException extends BankingException{
    public DailyLimitExceededException() {
        super("Daily transaction limit exceeded",
                "DAILY_LIMIT_EXCEEDED",
                HttpStatus.BAD_REQUEST);
    }
}
