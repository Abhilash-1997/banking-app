-- ================================================================
-- V4 — Create Ledger Tables (Double-Entry Bookkeeping)
-- ================================================================
--
-- The ledger module OWNS these tables.
--
-- Double-Entry Bookkeeping Principles:
-- 1. Money never appears or disappears from thin air; every movement
--    has a source (DEBIT) and a destination (CREDIT).
-- 2. Every financial event generates at least two entries:
--    Sum of Debits == Sum of Credits.
-- 3. IMMUTABILITY: Ledger entries are NEVER updated (UPDATE) or
--    deleted (DELETE). They are strictly append-only.
--    If an error occurs, an offsetting reversal entry is posted.
-- ================================================================

CREATE TABLE ledger_entries
(
    id               UUID                     NOT NULL DEFAULT gen_random_uuid(),

    -- Reference to the business transaction that triggered this entry
    transaction_id   UUID                     NOT NULL,

    -- The account affected by this entry
    account_id       UUID                     NOT NULL,

    -- DEBIT or CREDIT
    entry_type       VARCHAR(20)              NOT NULL,

    -- Exact monetary value (positive only)
    amount           NUMERIC(19, 4)           NOT NULL,

    -- ISO 4217 Currency Code
    currency         VARCHAR(3)               NOT NULL DEFAULT 'INR',

    -- Financial explanation (e.g. "Transfer debit to ACC-12345678")
    description      VARCHAR(255)             NOT NULL,

    -- Immutable timestamp of posting
    posted_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_ledger_entries PRIMARY KEY (id),
    CONSTRAINT chk_ledger_amount_positive CHECK (amount > 0)
);

-- Index for querying all ledger entries belonging to an account (e.g. statement generation)
CREATE INDEX idx_ledger_account_id ON ledger_entries (account_id);

-- Index for finding all entries tied to a transaction (to verify debit = credit balance)
CREATE INDEX idx_ledger_transaction_id ON ledger_entries (transaction_id);
