package com.bank.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawCommand(
        @NotNull(message = "Account ID is required")
        UUID accountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0001", message = "Withdrawal amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        String currency,

        String description
) {}
