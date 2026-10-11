-- ================================================================
-- V1 — Create Customer Tables
-- ================================================================
--
-- Flyway Migration Convention:
--   V{version}__{description}.sql
--
-- The customer module OWNS these tables.
-- No other module should write directly to these tables.
-- Cross-module data access happens through the CustomerService API
-- or domain events — NOT by querying customer_* tables directly
-- from another module.
--
-- This is a key principle of the Modular Monolith:
-- One database, but clear DATA OWNERSHIP per module.
-- ================================================================

CREATE TABLE customers
(
    -- Primary key: UUID for global uniqueness.
    -- We use UUID instead of auto-increment so IDs are safe to expose
    -- in URLs and don't reveal sequential record counts.
    id                  UUID                     NOT NULL DEFAULT gen_random_uuid(),

    -- Core identity
    first_name          VARCHAR(100)             NOT NULL,
    last_name           VARCHAR(100)             NOT NULL,
    email               VARCHAR(255)             NOT NULL,
    phone_number        VARCHAR(20),

    -- Customer lifecycle status.
    -- PENDING   → Registered but not yet verified
    -- ACTIVE    → Verified and can use the system
    -- SUSPENDED → Temporarily blocked
    -- CLOSED    → Account permanently closed
    status              VARCHAR(50)              NOT NULL DEFAULT 'PENDING',

    -- Audit columns — every table has these.
    -- created_at: when the record was first created (immutable after insert)
    -- updated_at: last modification timestamp
    -- created_by: who created it (system or user ID)
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255)             NOT NULL DEFAULT 'SYSTEM',

    -- Optimistic locking version column.
    -- Hibernate uses this to detect concurrent modifications.
    -- If two transactions read version=1 and both try to update,
    -- the second one will fail with OptimisticLockException.
    -- This prevents "lost update" bugs without expensive locks.
    version             BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT pk_customers PRIMARY KEY (id),
    CONSTRAINT uq_customers_email UNIQUE (email)
);

-- Index on email for fast login/lookup queries
CREATE INDEX idx_customers_email ON customers (email);

-- Index on status for filtering active customers
CREATE INDEX idx_customers_status ON customers (status);

-- ================================================================
-- Explanation: Why UUID?
-- ================================================================
-- Auto-increment IDs (1, 2, 3...) are predictable.
-- An attacker who sees /api/customers/42 knows customer 41 exists.
-- UUIDs are random and safe to expose in public APIs.
-- The trade-off: UUID primary keys are slightly larger and slightly
-- slower for index lookups, but for a banking app, security wins.
-- ================================================================
