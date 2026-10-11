package com.bank.transaction;

/**
 * State machine for financial transactions:
 *
 * INITIATED -> PROCESSING -> COMPLETED
 *                         \-> FAILED
 * COMPLETED -> REVERSED (e.g. chargeback/reversal)
 */
public enum TransactionStatus {
    INITIATED,
    PROCESSING,
    COMPLETED,
    FAILED,
    REVERSED
}
