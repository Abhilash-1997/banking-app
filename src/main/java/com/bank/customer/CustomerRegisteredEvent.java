package com.bank.customer;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event: A new customer has successfully registered.
 *
 * This event is PUBLIC — it's in the customer module's root package,
 * not in 'internal'. Other modules CAN and SHOULD listen to this event.
 *
 * Why an event instead of a direct call?
 *
 * Option A (direct call — tight coupling):
 *   customerService.register() → accountService.createDefaultAccount()
 *   Problem: CustomerService now DEPENDS ON AccountService.
 *   If AccountService changes its API, CustomerService breaks.
 *   Customer module can no longer be understood in isolation.
 *
 * Option B (event — loose coupling):
 *   customerService.register() → publishes CustomerRegisteredEvent
 *   AccountService LISTENS to CustomerRegisteredEvent
 *   CustomerService doesn't know AccountService exists.
 *   Dependencies flow: Account → Customer (account knows about customer)
 *   NOT: Customer → Account (customer does NOT know about account)
 *
 * This is the correct dependency direction for our domain:
 *   An Account belongs to a Customer.
 *   A Customer doesn't know it has Accounts.
 *
 * Event Design Rules:
 * - Events are IMMUTABLE (record in Java — no setters)
 * - Events describe something that ALREADY HAPPENED (past tense name)
 * - Events carry enough information for listeners to act without
 *   calling back into the source module for basic data
 * - Events should NOT carry full entity objects — carry IDs and
 *   minimal data that listeners actually need
 */
public record CustomerRegisteredEvent(

        // The new customer's ID — listeners can look up more details
        // from CustomerService if they need them.
        UUID customerId,

        // Email is included because the notification module needs it
        // to send a welcome email without a round-trip to CustomerService.
        String email,

        // Full name for personalized notifications.
        String fullName,

        // When did this happen?
        Instant occurredAt
) {
    /**
     * Convenience factory — creates the event with the current timestamp.
     */
    public static CustomerRegisteredEvent of(UUID customerId, String email, String fullName) {
        return new CustomerRegisteredEvent(customerId, email, fullName, Instant.now());
    }
}
