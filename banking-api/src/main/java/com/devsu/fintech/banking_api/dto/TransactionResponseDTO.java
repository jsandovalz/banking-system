package com.devsu.fintech.banking_api.dto;

import com.devsu.fintech.banking_api.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        LocalDateTime date,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balance,
        String accountNumber
) {}
