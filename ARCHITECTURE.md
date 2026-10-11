# ARCHITECTURE.md — Banking Application

## Overview

This document explains the architectural decisions made in this project.
It is updated with each phase.

---

## Architecture Style: Modular Monolith

We use a **Modular Monolith** architecture, enforced by **Spring Modulith**.

| Property | Traditional Monolith | Modular Monolith (This App) | Microservices |
|---|---|---|---|
| Deployment units | 1 | 1 | Many |
| Module boundaries | None enforced | Enforced by framework | Enforced by network |
| Cross-module calls | Direct (anything to anything) | Via public API or events | Network (REST/gRPC) |
| Database | Usually 1 shared | 1 shared (data ownership per module) | Separate per service |
| Transactions | Easy (@Transactional) | Easy (@Transactional across modules) | Hard (Saga/2PC/Outbox) |
| Operational complexity | Low | Low | High |
| Scalability | Whole app scales together | Whole app scales together | Independent scaling |
| Refactoring to microservices | Very hard (tangled) | Clean extraction path | Already there |

---

## Module Topology (Spring Modulith Detected)

```
com.bank
├── common       ← Cross-cutting: exceptions, DTOs, error handling (@NamedInterface api, exception)
├── customer     ← Customer profile, registration, KYC
├── account      ← Bank accounts, balance, account lifecycle
├── ledger       ← Immutable double-entry bookkeeping (Debit & Credit journal)
└── transaction  ← Financial operations lifecycle & idempotency coordinator
```

### Module Dependency Graph

```
transaction ──── uses API of ────► account (debit/credit)
transaction ──── uses API of ────► ledger (double-entry batch)
account     ──── uses API of ────► customer (exists check)
account     ──── listens to ─────► CustomerRegisteredEvent (from customer)
all modules ──── use ────────────► common (exceptions, ApiResponse)
```

**Directional discipline**:
- `customer` has ZERO dependencies on `account`, `transaction`, or `ledger`.
- `ledger` has ZERO dependencies on `transaction` or `account` (it only records pure financial journal entries).
- `transaction` coordinates `account` and `ledger` inside a single `@Transactional` boundary.

---

## Double-Entry Bookkeeping Principles (Ledger Module)

In accounting and banking:
1. **No money is created or destroyed without counter-entries**:
   $\sum \text{Debits} == \sum \text{Credits}$
2. **Immutable Financial History**:
   `ledger_entries` are **strictly append-only**. No `UPDATE` or `DELETE` statements are ever executed against the ledger table. If a correction is needed, an offsetting reversal entry is posted.
3. **Difference between Account Balance & Ledger**:
   - `account.balance` is a fast cached snapshot for querying and optimistic lock verification.
   - `ledger_entries` are the canonical legal source of truth. An account balance can always be re-derived by summing historical ledger entries.

---

## Financial Data Rules

1. **Never use `double` or `float` for money** — always use `BigDecimal` with `HALF_EVEN` (banker's) rounding.
2. **Database column: `NUMERIC(19, 4)`** — exact decimal arithmetic in PostgreSQL.
3. **Optimistic Locking (`@Version`)** on both `Account` and `Transaction` entities.
4. **Idempotency Protection**:
   `Idempotency-Key` prevents duplicate operations from accidental network retries or repeated user clicks.

---

## Flyway Migration Progression

```
V1__create_customer_tables.sql           ← customer module
V2__create_account_tables.sql            ← account module
V3__create_event_publication_table.sql   ← Spring Modulith Outbox registry
V4__create_ledger_tables.sql             ← ledger module (double-entry)
V5__create_transaction_tables.sql        ← transaction module (lifecycle & idempotency)
```

---

## Phase Roadmap

- [x] Phase 1: Architecture concepts (Monolith vs Modular Monolith vs Microservices)
- [x] Phase 2: Customer + Account modules, boundaries, Spring Modulith events
- [x] Phase 3: Transaction module + Ledger module (double-entry bookkeeping, idempotency, atomicity)
- [ ] Phase 4: Spring Security + JWT Authentication
- [ ] Phase 5: Kafka Integration & Event Externalization
- [ ] Phase 6: Rule-based Fraud Detection module
- [ ] Phase 7: Notification module (Email/SMS abstractions)
- [ ] Phase 8: Docker Compose & Kubernetes local manifests
- [ ] Phase 9: AWS Deployment Strategy
