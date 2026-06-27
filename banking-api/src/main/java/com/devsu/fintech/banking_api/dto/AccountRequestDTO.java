package com.devsu.fintech.banking_api.dto;

import com.devsu.fintech.banking_api.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountRequestDTO (
        @NotBlank @Size(max = 20)
        String accountNumber,

        @NotNull
        AccountType accountType,

        @NotNull @DecimalMin(value = "0.0", message = "Initial balance cannot be negative")
        BigDecimal initialBalance,

        @NotNull
        Boolean status,

        @NotBlank
        String clientId
){}