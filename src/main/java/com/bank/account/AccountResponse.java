package com.bank.account;

import com.bank.account.internal.Account;
import com.bank.account.internal.AccountStatus;
import com.bank.account.internal.AccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public DTO returned from AccountService.
 *
 * Notice: no entity classes in this record.
 * This is safe to return across module boundaries.
 */
public record AccountResponse(
        UUID id,
        UUID customerId,
        String accountNumber,
        AccountType accountType,
        AccountStatus status,
        BigDecimal balance,
        BigDecimal availableBalance,
        String currency,
        Instant createdAt
) {
    static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getCustomerId(),
            account.getAccountNumber(),
            account.getAccountType(),
            account.getStatus(),
            account.getBalance(),
            account.getAvailableBalance(),
            account.getCurrency(),
            account.getCreatedAt()
        );
    }
}
