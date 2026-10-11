package com.bank.account.internal;

/**
 * Account lifecycle states.
 *
 * PENDING  → Created, awaiting activation
 * ACTIVE   → Fully operational — can deposit, withdraw, transfer
 * FROZEN   → Temporarily blocked (fraud hold, legal order)
 * CLOSED   → Permanently closed — no further operations
 *
 * State machine:
 *   PENDING  → ACTIVE  (activation)
 *   ACTIVE   → FROZEN  (admin/fraud action)
 *   FROZEN   → ACTIVE  (admin unfreeze)
 *   ACTIVE   → CLOSED  (closure request)
 *   FROZEN   → CLOSED  (closure while frozen)
 */
public enum AccountStatus {
    PENDING,
    ACTIVE,
    FROZEN,
    CLOSED
}
