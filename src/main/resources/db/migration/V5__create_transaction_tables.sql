-- ================================================================
-- V5 — Create Transaction Tables
-- ================================================================
--
-- The transaction module OWNS these tables.
--
-- Responsibilities:
-- 1. Tracks the lifecycle of financial intent:
--    INITIATED -> PROCESSING -> COMPLETED or FAILED
-- 2. Idempotency Key: Prevents duplicate execution if a client retries
--    due to a network timeout or duplicate click.
-- 3. Human-readable Reference Number: e.g. "TXN-20261011-XXXXXXXX"
-- ================================================================

CREATE TABLE transactions
(
    id                 UUID                     NOT NULL DEFAULT gen_random_uuid(),

    -- Unique transaction reference code for public tracking and receipts
    reference_number   VARCHAR(50)              NOT NULL,

    -- Unique client idempotency key (header Idempotency-Key)
    idempotency_key    VARCHAR(100),

    -- Type: TRANSFER, DEPOSIT, WITHDRAWAL
    transaction_type   VARCHAR(50)              NOT NULL,

    -- Lifecycle: INITIATED, PROCESSING, COMPLETED, FAILED, REVERSED
    status             VARCHAR(50)              NOT NULL DEFAULT 'INITIATED',

    -- Accounts involved
    source_account_id  UUID,
    target_account_id  UUID,

    -- Monetary value
    amount             NUMERIC(19, 4)           NOT NULL,
    currency           VARCHAR(3)               NOT NULL DEFAULT 'INR',

    -- Metadata & Audit
    description        VARCHAR(255),
    failure_reason     TEXT,

    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Optimistic locking for transaction record state transitions
    version            BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT uq_transactions_ref UNIQUE (reference_number),
    CONSTRAINT uq_transactions_idempotency UNIQUE (idempotency_key)
);

-- Index for searching transactions by source account
CREATE INDEX idx_transactions_source_acc ON transactions (source_account_id);

-- Index for searching transactions by target account
CREATE INDEX idx_transactions_target_acc ON transactions (target_account_id);

-- Index for fast idempotency lookups
CREATE INDEX idx_transactions_idempotency ON transactions (idempotency_key);
