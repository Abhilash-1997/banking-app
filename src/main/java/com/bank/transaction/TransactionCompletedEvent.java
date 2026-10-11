package com.bank.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event published whenever a transaction successfully completes.
 *
 * Future modules (Notification, Audit, Fraud Detection) will listen to this event.
 */
public record TransactionCompletedEvent(
        UUID transactionId,
        String referenceNumber,
        TransactionType transactionType,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        String currency,
        Instant completedAt
) {
    public static TransactionCompletedEvent of(UUID transactionId,
                                                String referenceNumber,
                                                TransactionType type,
                                                UUID sourceAccountId,
                                                UUID targetAccountId,
                                                BigDecimal amount,
                                                String currency) {
        return new TransactionCompletedEvent(
                transactionId,
                referenceNumber,
                type,
                sourceAccountId,
                targetAccountId,
                amount,
                currency,
                Instant.now()
        );
    }
}
