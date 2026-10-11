package com.bank.account;

import com.bank.account.internal.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command for creating a new bank account.
 *
 * The customerId is provided by the controller (extracted from
 * the authenticated user's JWT token in future phases).
 * For now, it's passed in the request body.
 */
public record CreateAccountCommand(

        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotBlank(message = "Currency is required")
        String currency
) {}
