package com.bank.account.internal;

import com.bank.common.exception.BankingException;

import java.math.BigDecimal;

/**
 * Thrown when a withdrawal or transfer would result in a balance
 * below the account's minimum balance requirement.
 *
 * This is a domain-level exception — it represents a business rule violation.
 * The GlobalExceptionHandler maps this to HTTP 400 Bad Request.
 *
 * Note: This class is in 'internal' but extends BankingException (from common).
 * The exception TYPE is internal, but the base class is shared.
 * External modules that catch BankingException will still handle this correctly
 * without needing to know about InsufficientBalanceException specifically.
 */
public class InsufficientBalanceException extends BankingException {

    public InsufficientBalanceException(String accountNumber, BigDecimal currentBalance,
                                         BigDecimal requestedAmount, BigDecimal minimumBalance) {
        super(
            "INSUFFICIENT_BALANCE",
            String.format(
                "Insufficient balance in account %s. Current: %s, Requested: %s, Minimum required: %s",
                accountNumber, currentBalance, requestedAmount, minimumBalance
            )
        );
    }
}
