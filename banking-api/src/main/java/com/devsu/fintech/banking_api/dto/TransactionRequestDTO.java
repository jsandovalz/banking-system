package com.devsu.fintech.banking_api.dto;

import com.devsu.fintech.banking_api.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @NotBlank
        String accountNumber,

        @NotNull
        TransactionType transactionType,

        @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
) {}