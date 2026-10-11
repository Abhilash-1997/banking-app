package com.bank.ledger;

import com.bank.common.exception.ValidationException;
import com.bank.ledger.internal.LedgerEntry;
import com.bank.ledger.internal.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * LedgerService — the PUBLIC API of the ledger module.
 *
 * Implements Double-Entry Bookkeeping Verification:
 * For every internal transfer batch, sum(DEBIT) must equal sum(CREDIT).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    /**
     * Records a batch of double-entry ledger items for a transaction.
     * Enforces that the transaction is balanced (sum(Debits) == sum(Credits))
     * when there are both debit and credit entries.
     */
    @Transactional
    public List<LedgerEntryResponse> recordDoubleEntry(UUID transactionId, List<RecordEntryCommand> commands) {
        log.info("Posting {} ledger entries for transaction {}", commands.size(), transactionId);

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (RecordEntryCommand cmd : commands) {
            if (cmd.entryType() == EntryType.DEBIT) {
                totalDebits = totalDebits.add(cmd.amount());
            } else if (cmd.entryType() == EntryType.CREDIT) {
                totalCredits = totalCredits.add(cmd.amount());
            }
        }

        // For transfers with 2 or more entries, ensure books balance: Debits == Credits
        if (commands.size() >= 2 && totalDebits.compareTo(totalCredits) != 0) {
            throw new ValidationException(
                    "LEDGER_UNBALANCED",
                    String.format("Double-entry bookkeeping imbalance! Total Debits: %s, Total Credits: %s",
                            totalDebits, totalCredits)
            );
        }

        List<LedgerEntry> entries = commands.stream()
                .map(cmd -> LedgerEntry.create(
                        transactionId,
                        cmd.accountId(),
                        cmd.entryType(),
                        cmd.amount(),
                        cmd.currency(),
                        cmd.description()
                ))
                .toList();

        List<LedgerEntry> savedEntries = ledgerRepository.saveAll(entries);
        log.info("Successfully persisted {} ledger entries for transaction {}", savedEntries.size(), transactionId);

        return savedEntries.stream()
                .map(LedgerEntryResponse::from)
                .toList();
    }

    /**
     * Query immutable audit history of an account.
     */
    @Transactional(readOnly = true)
    public List<LedgerEntryResponse> getEntriesByAccount(UUID accountId) {
        return ledgerRepository.findByAccountIdOrderByPostedAtDesc(accountId)
                .stream()
                .map(LedgerEntryResponse::from)
                .toList();
    }

    /**
     * Query all entries associated with a transaction.
     */
    @Transactional(readOnly = true)
    public List<LedgerEntryResponse> getEntriesByTransaction(UUID transactionId) {
        return ledgerRepository.findByTransactionIdOrderByPostedAtAsc(transactionId)
                .stream()
                .map(LedgerEntryResponse::from)
                .toList();
    }
}
