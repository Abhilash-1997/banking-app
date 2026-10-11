package com.bank.transaction.internal;

import com.bank.transaction.TransactionStatus;
import com.bank.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction JPA Entity — INTERNAL to the transaction module.
 *
 * Implements transaction lifecycle tracking and idempotency safeguards.
 */
@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TransactionStatus status;

    @Column(name = "source_account_id")
    private UUID sourceAccountId;

    @Column(name = "target_account_id")
    private UUID targetAccountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public static Transaction initiate(String referenceNumber,
                                        String idempotencyKey,
                                        TransactionType type,
                                        UUID sourceAccountId,
                                        UUID targetAccountId,
                                        BigDecimal amount,
                                        String currency,
                                        String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        Transaction tx = new Transaction();
        tx.referenceNumber = referenceNumber;
        tx.idempotencyKey = idempotencyKey;
        tx.transactionType = type;
        tx.status = TransactionStatus.INITIATED;
        tx.sourceAccountId = sourceAccountId;
        tx.targetAccountId = targetAccountId;
        tx.amount = amount.setScale(4, java.math.RoundingMode.HALF_EVEN);
        tx.currency = currency;
        tx.description = description;
        tx.createdAt = Instant.now();
        tx.updatedAt = Instant.now();
        return tx;
    }

    public void markProcessing() {
        this.status = TransactionStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }
}
