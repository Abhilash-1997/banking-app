package com.bank.transaction;

import com.bank.account.AccountResponse;
import com.bank.account.AccountService;
import com.bank.common.exception.ResourceNotFoundException;
import com.bank.common.exception.ValidationException;
import com.bank.ledger.EntryType;
import com.bank.ledger.LedgerService;
import com.bank.ledger.RecordEntryCommand;
import com.bank.transaction.internal.Transaction;
import com.bank.transaction.internal.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TransactionService — the PUBLIC API of the transaction module.
 *
 * Coordinates:
 * 1. Idempotency handling
 * 2. Multi-module atomic financial execution (Account + Ledger)
 * 3. Double-entry bookkeeping dispatch
 * 4. Transaction lifecycle state management
 * 5. Domain event publication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final LedgerService ledgerService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Executes an account-to-account transfer.
     * Atomically coordinates Account balance adjustment and Ledger entry generation.
     */
    @Transactional
    public TransactionResponse transfer(TransferCommand command, String idempotencyKey) {
        log.info("Processing transfer from {} to {}, amount: {} {}",
                command.sourceAccountId(), command.targetAccountId(), command.amount(), command.currency());

        // 1. Idempotency Safeguard
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                log.info("Idempotent request detected for key {}. Returning existing transaction {}",
                        idempotencyKey, existing.get().getReferenceNumber());
                return TransactionResponse.from(existing.get());
            }
        }

        // 2. Preconditions
        if (command.sourceAccountId().equals(command.targetAccountId())) {
            throw new ValidationException("INVALID_TRANSFER", "Source and destination accounts cannot be the same");
        }

        // Validate both accounts exist
        AccountResponse sourceAccount = accountService.findById(command.sourceAccountId());
        AccountResponse targetAccount = accountService.findById(command.targetAccountId());

        String refNumber = generateReferenceNumber();

        // 3. Initiate Transaction Record
        Transaction tx = Transaction.initiate(
                refNumber,
                idempotencyKey,
                TransactionType.TRANSFER,
                sourceAccount.id(),
                targetAccount.id(),
                command.amount(),
                command.currency(),
                command.description()
        );
        tx.markProcessing();
        Transaction savedTx = transactionRepository.save(tx);

        try {
            // 4. Debit source account & Credit destination account (protected by @Version optimistic locking)
            accountService.debit(sourceAccount.id(), command.amount());
            accountService.credit(targetAccount.id(), command.amount());

            // 5. Create Double-Entry Ledger records
            List<RecordEntryCommand> entries = List.of(
                    new RecordEntryCommand(
                            sourceAccount.id(),
                            EntryType.DEBIT,
                            command.amount(),
                            command.currency(),
                            "Transfer outbound to " + targetAccount.accountNumber()
                    ),
                    new RecordEntryCommand(
                            targetAccount.id(),
                            EntryType.CREDIT,
                            command.amount(),
                            command.currency(),
                            "Transfer inbound from " + sourceAccount.accountNumber()
                    )
            );
            ledgerService.recordDoubleEntry(savedTx.getId(), entries);

            // 6. Complete Transaction
            savedTx.markCompleted();
            transactionRepository.save(savedTx);

            // 7. Publish Event
            eventPublisher.publishEvent(
                    TransactionCompletedEvent.of(
                            savedTx.getId(),
                            savedTx.getReferenceNumber(),
                            TransactionType.TRANSFER,
                            sourceAccount.id(),
                            targetAccount.id(),
                            command.amount(),
                            command.currency()
                    )
            );

            log.info("Transfer {} completed successfully", refNumber);
            return TransactionResponse.from(savedTx);

        } catch (Exception ex) {
            log.error("Transfer {} failed: {}", refNumber, ex.getMessage());
            savedTx.markFailed(ex.getMessage());
            transactionRepository.save(savedTx);
            throw ex;
        }
    }

    /**
     * Executes a deposit into an account.
     */
    @Transactional
    public TransactionResponse deposit(DepositCommand command, String idempotencyKey) {
        log.info("Processing deposit to account {}, amount: {}", command.accountId(), command.amount());

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return TransactionResponse.from(existing.get());
            }
        }

        AccountResponse account = accountService.findById(command.accountId());
        String refNumber = generateReferenceNumber();

        Transaction tx = Transaction.initiate(
                refNumber,
                idempotencyKey,
                TransactionType.DEPOSIT,
                null,
                account.id(),
                command.amount(),
                command.currency(),
                command.description()
        );
        tx.markProcessing();
        Transaction savedTx = transactionRepository.save(tx);

        accountService.credit(account.id(), command.amount());

        ledgerService.recordDoubleEntry(savedTx.getId(), List.of(
                new RecordEntryCommand(
                        account.id(),
                        EntryType.CREDIT,
                        command.amount(),
                        command.currency(),
                        "Deposit: " + (command.description() != null ? command.description() : "Cash/Cheque")
                )
        ));

        savedTx.markCompleted();
        transactionRepository.save(savedTx);

        eventPublisher.publishEvent(
                TransactionCompletedEvent.of(
                        savedTx.getId(),
                        savedTx.getReferenceNumber(),
                        TransactionType.DEPOSIT,
                        null,
                        account.id(),
                        command.amount(),
                        command.currency()
                )
        );

        return TransactionResponse.from(savedTx);
    }

    /**
     * Executes a withdrawal from an account.
     */
    @Transactional
    public TransactionResponse withdraw(WithdrawCommand command, String idempotencyKey) {
        log.info("Processing withdrawal from account {}, amount: {}", command.accountId(), command.amount());

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return TransactionResponse.from(existing.get());
            }
        }

        AccountResponse account = accountService.findById(command.accountId());
        String refNumber = generateReferenceNumber();

        Transaction tx = Transaction.initiate(
                refNumber,
                idempotencyKey,
                TransactionType.WITHDRAWAL,
                account.id(),
                null,
                command.amount(),
                command.currency(),
                command.description()
        );
        tx.markProcessing();
        Transaction savedTx = transactionRepository.save(tx);

        accountService.debit(account.id(), command.amount());

        ledgerService.recordDoubleEntry(savedTx.getId(), List.of(
                new RecordEntryCommand(
                        account.id(),
                        EntryType.DEBIT,
                        command.amount(),
                        command.currency(),
                        "Withdrawal: " + (command.description() != null ? command.description() : "ATM/Branch")
                )
        ));

        savedTx.markCompleted();
        transactionRepository.save(savedTx);

        eventPublisher.publishEvent(
                TransactionCompletedEvent.of(
                        savedTx.getId(),
                        savedTx.getReferenceNumber(),
                        TransactionType.WITHDRAWAL,
                        account.id(),
                        null,
                        command.amount(),
                        command.currency()
                )
        );

        return TransactionResponse.from(savedTx);
    }

    @Transactional(readOnly = true)
    public TransactionResponse findByReference(String referenceNumber) {
        Transaction tx = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", referenceNumber));
        return TransactionResponse.from(tx);
    }

    private String generateReferenceNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = ThreadLocalRandom.current().nextInt(10000000, 99999999);
        return "TXN-" + datePart + "-" + randomPart;
    }
}
