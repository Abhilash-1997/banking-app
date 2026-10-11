package com.bank.account.internal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Account JPA Entity — INTERNAL to the account module.
 *
 * Key financial design decisions:
 *
 * 1. BigDecimal for money, not double/float.
 *    BigDecimal stores exact decimal values.
 *    double 0.1 + double 0.2 = 0.30000000000000004 (IEEE 754 imprecision)
 *    BigDecimal("0.1") + BigDecimal("0.2") = 0.3 (exact)
 *
 * 2. @Version for optimistic locking on balance.
 *    Without version control, this race condition is possible:
 *      Thread 1: reads balance=₹10,000
 *      Thread 2: reads balance=₹10,000
 *      Thread 1: withdraws ₹8,000, saves balance=₹2,000
 *      Thread 2: withdraws ₹8,000, saves balance=₹2,000 (WRONG! should fail)
 *    With @Version:
 *      Thread 1 saves → version 1→2
 *      Thread 2 tries to save with old version=1 → OptimisticLockException
 *
 * 3. No direct setters for balance.
 *    Balance changes only happen through domain methods (deposit, withdraw).
 *    This prevents accidental balance mutations from anywhere in the code.
 */
@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the owning customer.
     * This is just a UUID — not a JPA @ManyToOne relationship.
     *
     * Why not @ManyToOne Customer?
     * Because Customer is in a different module's 'internal' package.
     * Referencing it with @ManyToOne would create a hard dependency
     * between Account's persistence layer and Customer's entity class.
     * We store the ID only and call CustomerService when we need details.
     */
    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 50)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AccountStatus status;

    /**
     * Account balance.
     *
     * NUMERIC(19, 4) in PostgreSQL:
     * - 19 significant digits total (handles values up to 999 trillion)
     * - Always 4 decimal places (₹10.0000, ₹1234.5678)
     *
     * Java mapping: BigDecimal with scale=4
     * We use HALF_EVEN rounding (banker's rounding) for any calculations.
     */
    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "minimum_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal minimumBalance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    /**
     * Optimistic locking version — CRITICAL for concurrent balance updates.
     * See class-level comment for the race condition this prevents.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Static factory — creates a new account in PENDING status.
     * Accounts are PENDING until explicitly activated.
     */
    public static Account create(UUID customerId, String accountNumber,
                                  AccountType accountType, String currency) {
        Account account = new Account();
        account.customerId = customerId;
        account.accountNumber = accountNumber;
        account.accountType = accountType;
        account.status = AccountStatus.PENDING;
        account.balance = BigDecimal.ZERO.setScale(4);
        account.currency = currency;
        account.minimumBalance = BigDecimal.ZERO.setScale(4);
        account.createdAt = Instant.now();
        account.updatedAt = Instant.now();
        account.createdBy = "SYSTEM";
        return account;
    }

    /**
     * Activate the account — transitions PENDING → ACTIVE.
     */
    public void activate() {
        if (this.status != AccountStatus.PENDING) {
            throw new IllegalStateException(
                "Cannot activate account in status: " + this.status
            );
        }
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Deposit money into the account.
     *
     * Note: Only ACTIVE accounts can receive deposits.
     * Amount must be positive.
     *
     * We use BigDecimal.add() — never + operator for money.
     */
    public void deposit(BigDecimal amount) {
        validateActive();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount).setScale(4, java.math.RoundingMode.HALF_EVEN);
        this.updatedAt = Instant.now();
    }

    /**
     * Withdraw money from the account.
     *
     * Checks:
     * 1. Account must be ACTIVE
     * 2. Amount must be positive
     * 3. Resulting balance must not go below minimum balance
     */
    public void withdraw(BigDecimal amount) {
        validateActive();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        BigDecimal resultingBalance = this.balance.subtract(amount);
        if (resultingBalance.compareTo(this.minimumBalance) < 0) {
            throw new InsufficientBalanceException(
                this.accountNumber, this.balance, amount, this.minimumBalance
            );
        }
        this.balance = resultingBalance.setScale(4, java.math.RoundingMode.HALF_EVEN);
        this.updatedAt = Instant.now();
    }

    public BigDecimal getAvailableBalance() {
        return this.balance.subtract(this.minimumBalance);
    }

    private void validateActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "Account " + this.accountNumber + " is not active. Status: " + this.status
            );
        }
    }
}
