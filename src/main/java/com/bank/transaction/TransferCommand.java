package com.bank.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for initiating an account-to-account transfer.
 */
public record TransferCommand(
        @NotNull(message = "Source account ID is required")
        UUID sourceAccountId,

        @NotNull(message = "Target account ID is required")
        UUID targetAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0001", message = "Transfer amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        String currency,

        String description
) {}
