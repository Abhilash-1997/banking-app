package com.bank.ledger.internal;

import com.bank.ledger.EntryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * LedgerEntry JPA Entity — INTERNAL to the ledger module.
 *
 * Immutability Principle:
 * - Financial records must never be updated or deleted.
 * - This entity has NO setters and NO update methods.
 * - Once written, it stands forever as legal financial history.
 * - Precision: NUMERIC(19, 4) using BigDecimal.
 */
@Entity
@Table(name = "ledger_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "account_id", updatable = false, nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", updatable = false, nullable = false, length = 20)
    private EntryType entryType;

    @Column(name = "amount", updatable = false, nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", updatable = false, nullable = false, length = 3)
    private String currency;

    @Column(name = "description", updatable = false, nullable = false, length = 255)
    private String description;

    @Column(name = "posted_at", updatable = false, nullable = false)
    private Instant postedAt;

    public static LedgerEntry create(UUID transactionId,
                                      UUID accountId,
                                      EntryType entryType,
                                      BigDecimal amount,
                                      String currency,
                                      String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ledger entry amount must be positive");
        }

        LedgerEntry entry = new LedgerEntry();
        entry.transactionId = transactionId;
        entry.accountId = accountId;
        entry.entryType = entryType;
        entry.amount = amount.setScale(4, java.math.RoundingMode.HALF_EVEN);
        entry.currency = currency;
        entry.description = description;
        entry.postedAt = Instant.now();
        return entry;
    }
}
