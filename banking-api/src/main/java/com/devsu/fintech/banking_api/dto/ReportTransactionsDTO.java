package com.devsu.fintech.banking_api.dto;

import com.devsu.fintech.banking_api.model.AccountType;
import com.devsu.fintech.banking_api.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReportTransactionsDTO {
    public record TransactionDetail(
            LocalDateTime date,
            String client,
            String accountNumber,
            AccountType accountType,
            BigDecimal initialBalance,
            Boolean status,
            TransactionType transactionType,
            BigDecimal amount,
            BigDecimal balance
    ) { }

    public record Resume(
            String client,
            LocalDate from,
            LocalDate to,
            BigDecimal totalCredits,
            BigDecimal totalDebits,
            List<TransactionDetail> transactions
    ) { }

    public record StatusAcountResponse(
            Resume report,
            String pdfBase64
    ){}
}
