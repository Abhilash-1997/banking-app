package com.bank.ledger.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * LedgerRepository — INTERNAL data access for ledger entries.
 */
@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByAccountIdOrderByPostedAtDesc(UUID accountId);

    List<LedgerEntry> findByTransactionIdOrderByPostedAtAsc(UUID transactionId);
}
