package com.devsu.fintech.banking_api.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@ConfigurationProperties(prefix = "banking.cashout")
public record BankingProperties(@NotNull BigDecimal dailyLimit) {
}
