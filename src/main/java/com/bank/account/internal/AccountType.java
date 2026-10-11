package com.bank.account.internal;

/**
 * Types of bank accounts supported by the system.
 *
 * SAVINGS  — Regular savings account. Earns interest. Has minimum balance.
 * CURRENT  — Business/current account. No interest. Higher transaction limits.
 * FIXED_DEPOSIT — Locked for a term. Higher interest rate.
 * RECURRING_DEPOSIT — Regular periodic deposits. Fixed tenure.
 */
public enum AccountType {
    SAVINGS,
    CURRENT,
    FIXED_DEPOSIT,
    RECURRING_DEPOSIT
}
