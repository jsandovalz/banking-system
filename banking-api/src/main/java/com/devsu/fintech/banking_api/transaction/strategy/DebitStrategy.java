package com.devsu.fintech.banking_api.transaction.strategy;

import com.devsu.fintech.banking_api.config.BankingProperties;
import com.devsu.fintech.banking_api.excepcion.BalanceNotFoundException;
import com.devsu.fintech.banking_api.excepcion.DailyLimitExceededException;
import com.devsu.fintech.banking_api.model.Account;
import com.devsu.fintech.banking_api.model.TransactionType;
import com.devsu.fintech.banking_api.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class DebitStrategy implements TransactionStrategy {
    private final BankingProperties bankingProperties;
    private final TransactionRepository transactionRepository;


    @Override
    public TransactionType getType() {
        return TransactionType.RETIRO;
    }

    @Override
    public BigDecimal apply(Account account, BigDecimal amount) {
        BigDecimal balance = account.getAvailableBalance();
        if(balance.compareTo(BigDecimal.ZERO) == 0 || balance.compareTo(amount) < 0) {
            throw new BalanceNotFoundException(account.getId());
        }
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        BigDecimal withdrawnToday = transactionRepository.sumDailyTransactions(
                account.getAccountNumber(), TransactionType.RETIRO, startOfDay, endOfDay);

        if(withdrawnToday == null) {
            withdrawnToday = BigDecimal.ZERO;
        }
        if(withdrawnToday.add(amount).compareTo(bankingProperties.dailyLimit()) > 0) {
            throw new DailyLimitExceededException();
        }

        BigDecimal newBalance = balance.subtract(amount);
        account.setAvailableBalance(newBalance);
        return newBalance;
    }
}
