package com.bank.transaction;

import com.bank.transaction.internal.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public DTO for Transaction representations.
 */
public record TransactionResponse(
        UUID id,
        String referenceNumber,
        String idempotencyKey,
        TransactionType transactionType,
        TransactionStatus status,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        String currency,
        String description,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getReferenceNumber(),
                tx.getIdempotencyKey(),
                tx.getTransactionType(),
                tx.getStatus(),
                tx.getSourceAccountId(),
                tx.getTargetAccountId(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getDescription(),
                tx.getFailureReason(),
                tx.getCreatedAt(),
                tx.getUpdatedAt()
        );
    }
}
