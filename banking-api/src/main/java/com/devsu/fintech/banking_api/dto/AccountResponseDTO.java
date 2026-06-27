package com.devsu.fintech.banking_api.dto;

import com.devsu.fintech.banking_api.model.AccountType;

import java.math.BigDecimal;

public record AccountResponseDTO (
        Long id,
        String accountNumber,
        AccountType accountType,
        BigDecimal initialBalance,
        BigDecimal availableBalance,
        Boolean status,
        String clientId,
        String clientName
){}
