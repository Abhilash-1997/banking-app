package com.bank.customer.internal;

/**
 * Customer lifecycle states.
 *
 * PENDING   → Registered, awaiting verification
 * ACTIVE    → Verified, fully operational
 * SUSPENDED → Temporarily blocked (e.g., fraud investigation)
 * CLOSED    → Permanently closed, no further operations allowed
 *
 * State transitions:
 *   PENDING → ACTIVE (after verification)
 *   ACTIVE  → SUSPENDED (admin action)
 *   SUSPENDED → ACTIVE (admin action)
 *   ACTIVE / SUSPENDED → CLOSED (closure request)
 */
public enum CustomerStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    CLOSED
}
