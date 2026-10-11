package com.bank.ledger;

import com.bank.ledger.internal.LedgerEntry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public DTO for Ledger entries.
 */
public record LedgerEntryResponse(
        UUID id,
        UUID transactionId,
        UUID accountId,
        EntryType entryType,
        BigDecimal amount,
        String currency,
        String description,
        Instant postedAt
) {
    public static LedgerEntryResponse from(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.getId(),
                entry.getTransactionId(),
                entry.getAccountId(),
                entry.getEntryType(),
                entry.getAmount(),
                entry.getCurrency(),
                entry.getDescription(),
                entry.getPostedAt()
        );
    }
}
