package com.bank.account;

import com.bank.account.internal.*;
import com.bank.common.exception.ResourceNotFoundException;
import com.bank.common.exception.ValidationException;
import com.bank.customer.CustomerRegisteredEvent;
import com.bank.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * AccountService — the PUBLIC API of the account module.
 *
 * Module Communication Demonstration:
 *
 * This service communicates with the customer module in TWO ways:
 *
 * WAY 1: Direct API call (synchronous)
 *   When creating an account, we verify the customer exists.
 *   We call customerService.existsById() — the CustomerService public method.
 *   We do NOT access CustomerRepository directly.
 *
 *   Account module → CustomerService (public API) ✓
 *   Account module → CustomerRepository (internal)  ✗ FORBIDDEN
 *
 * WAY 2: Application Event (asynchronous, loose coupling)
 *   @ApplicationModuleListener on onCustomerRegistered().
 *   When a CustomerRegisteredEvent is published, Spring Modulith
 *   automatically calls this method AFTER the publishing transaction commits.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CustomerService customerService;

    /**
     * Listen for CustomerRegisteredEvent and auto-create a default SAVINGS account.
     */
    @ApplicationModuleListener
    public void onCustomerRegistered(CustomerRegisteredEvent event) {
        log.info("CustomerRegisteredEvent received for customer: {}. Creating default savings account.",
            event.customerId());

        CreateAccountCommand command = new CreateAccountCommand(
            event.customerId(),
            AccountType.SAVINGS,
            "INR"
        );

        AccountResponse account = createAccount(command);
        log.info("Default savings account {} created for customer {}",
            account.accountNumber(), event.customerId());
    }

    /**
     * Create a new bank account.
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountCommand command) {
        log.info("Creating {} account for customer: {}", command.accountType(), command.customerId());

        if (!customerService.existsById(command.customerId())) {
            throw new ValidationException(
                "CUSTOMER_NOT_FOUND",
                "Customer not found: " + command.customerId()
            );
        }

        String accountNumber = accountNumberGenerator.generate();

        Account account = Account.create(
            command.customerId(),
            accountNumber,
            command.accountType(),
            command.currency()
        );

        Account saved = accountRepository.save(account);
        log.info("Account created: {} ({})", saved.getAccountNumber(), saved.getId());

        return AccountResponse.from(saved);
    }

    /**
     * Retrieve all accounts for a customer.
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findByCustomerId(UUID customerId) {
        return accountRepository.findByCustomerId(customerId)
            .stream()
            .map(AccountResponse::from)
            .toList();
    }

    /**
     * Retrieve a specific account by ID.
     */
    @Transactional(readOnly = true)
    public AccountResponse findById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        return AccountResponse.from(account);
    }

    /**
     * Retrieve a specific account by Account Number.
     */
    @Transactional(readOnly = true)
    public AccountResponse findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountNumber));
        return AccountResponse.from(account);
    }

    /**
     * Activate a PENDING account.
     */
    @Transactional
    public AccountResponse activate(UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        account.activate();
        Account saved = accountRepository.save(account);

        log.info("Account {} activated", saved.getAccountNumber());
        return AccountResponse.from(saved);
    }

    /**
     * Debit funds from an account.
     * Enforces optimistic locking and domain constraints (sufficient funds, active account).
     */
    @Transactional
    public AccountResponse debit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        account.withdraw(amount);
        Account saved = accountRepository.save(account);

        log.info("Debited {} from account {}. New balance: {}", amount, account.getAccountNumber(), saved.getBalance());
        return AccountResponse.from(saved);
    }

    /**
     * Credit funds to an account.
     * Enforces optimistic locking and domain constraints (active account, positive amount).
     */
    @Transactional
    public AccountResponse credit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        account.deposit(amount);
        Account saved = accountRepository.save(account);

        log.info("Credited {} to account {}. New balance: {}", amount, account.getAccountNumber(), saved.getBalance());
        return AccountResponse.from(saved);
    }
}
