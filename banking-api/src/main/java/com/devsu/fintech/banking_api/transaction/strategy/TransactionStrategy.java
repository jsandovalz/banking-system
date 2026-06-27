package com.devsu.fintech.banking_api.transaction.strategy;

import com.devsu.fintech.banking_api.model.Account;
import com.devsu.fintech.banking_api.model.TransactionType;

import java.math.BigDecimal;

public interface TransactionStrategy {
    TransactionType getType();
    BigDecimal apply(Account account, BigDecimal amount);
}
