package com.bank.ledger;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for posting an entry to the ledger.
 */
public record RecordEntryCommand(
        @NotNull UUID accountId,
        @NotNull EntryType entryType,
        @NotNull @DecimalMin(value = "0.0001", message = "Amount must be greater than zero") BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String description
) {}
