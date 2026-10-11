package com.bank.customer;

import com.bank.common.exception.ResourceNotFoundException;
import com.bank.common.exception.ValidationException;
import com.bank.customer.internal.Customer;
import com.bank.customer.internal.CustomerRepository;
import com.bank.customer.internal.CustomerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * CustomerService — the PUBLIC API of the customer module.
 *
 * This is what other modules interact with. The methods on this class
 * are carefully designed to expose only what other modules need,
 * without leaking internal implementation details.
 *
 * Notice:
 * - Return types are CustomerResponse (a DTO), NOT Customer (the entity).
 * - No module outside 'customer' ever touches the Customer entity.
 * - The ApplicationEventPublisher is injected to publish domain events.
 *
 * Transaction Strategy:
 * - @Transactional on methods that modify state
 * - @Transactional(readOnly = true) on query methods for performance
 *   (readOnly=true tells Hibernate to skip dirty checking, which is
 *    faster for read-only operations)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Register a new customer.
     *
     * Transaction boundary: This entire method runs in ONE database transaction.
     * Both the customer INSERT and the event publication happen atomically.
     *
     * Spring Modulith's event infrastructure integrates with transactions:
     * the event is published AFTER the transaction commits successfully.
     * If the transaction rolls back (e.g., due to a DB error), the event
     * is NOT published. This prevents ghost events for failed operations.
     */
    @Transactional
    public CustomerResponse register(RegisterCustomerCommand command) {
        log.info("Registering new customer with email: {}", command.email());

        // Business rule: Email must be unique
        if (customerRepository.existsByEmail(command.email().toLowerCase().trim())) {
            throw new ValidationException(
                "EMAIL_ALREADY_EXISTS",
                "A customer with email '" + command.email() + "' already exists"
            );
        }

        // Create the customer through the domain factory method
        // We never use 'new Customer()' directly — the factory enforces invariants
        Customer customer = Customer.create(
            command.firstName(),
            command.lastName(),
            command.email(),
            command.phoneNumber()
        );

        Customer saved = customerRepository.save(customer);
        log.info("Customer registered with ID: {}", saved.getId());

        // Publish the domain event.
        // Spring will deliver this event to all @ApplicationModuleListener
        // methods AFTER this transaction commits.
        //
        // Who listens? Currently: AccountService (to create a default account).
        // In the future: NotificationService (to send welcome email).
        // CustomerService doesn't need to know who listens — that's the
        // point of event-driven communication.
        eventPublisher.publishEvent(
            CustomerRegisteredEvent.of(
                saved.getId(),
                saved.getEmail(),
                saved.getFullName()
            )
        );

        return CustomerResponse.from(saved);
    }

    /**
     * Find a customer by ID.
     *
     * readOnly = true:
     * - Hibernate skips dirty checking (no need to track changes)
     * - PostgreSQL receives "SET TRANSACTION READ ONLY" (can use read replicas)
     * - Slightly better performance for read operations
     */
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        return CustomerResponse.from(customer);
    }

    /**
     * Check if a customer exists by ID.
     * Used by other modules (e.g., Account) to validate a customer reference.
     *
     * Notice: returns boolean, not Customer.
     * The Account module doesn't get access to Customer data through this.
     */
    @Transactional(readOnly = true)
    public boolean existsById(UUID customerId) {
        return customerRepository.existsById(customerId);
    }

    /**
     * Activate a PENDING customer.
     * In a full system, this would be called after email verification.
     */
    @Transactional
    public CustomerResponse activate(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        customer.activate(); // domain method enforces state machine
        Customer saved = customerRepository.save(customer);

        log.info("Customer {} activated", customerId);
        return CustomerResponse.from(saved);
    }
}
