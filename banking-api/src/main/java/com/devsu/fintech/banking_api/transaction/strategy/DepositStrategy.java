package com.devsu.fintech.banking_api.transaction.strategy;

import com.devsu.fintech.banking_api.model.Account;
import com.devsu.fintech.banking_api.model.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositStrategy implements TransactionStrategy {
    @Override
    public TransactionType getType() {
        return TransactionType.DEPOSITO;
    }

    @Override
    public BigDecimal apply(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getAvailableBalance().add(amount);
        account.setAvailableBalance(newBalance);
        return newBalance;
    }
}
