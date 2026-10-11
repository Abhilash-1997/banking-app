package com.bank.customer;

import com.bank.customer.internal.Customer;
import com.bank.customer.internal.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Customer DTO — the public face of a Customer.
 *
 * This is what gets returned from CustomerService methods and
 * serialized into JSON for API responses.
 *
 * Why a separate DTO instead of returning the entity directly?
 *
 * 1. ENCAPSULATION: The Customer entity is in 'internal'. We can't
 *    return it from public methods without leaking the internal type.
 *
 * 2. CONTROL: DTOs let us choose exactly what to expose.
 *    The entity might have a password hash field — we'd never want
 *    that in the API response. The DTO is explicit about what's safe.
 *
 * 3. EVOLUTION: The entity schema can change without changing the API
 *    contract. We control the mapping in the 'from' factory method.
 *
 * The 'from' factory method lives here so the mapping logic is
 * encapsulated within the customer module.
 */
public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        CustomerStatus status,
        Instant createdAt
) {
    /**
     * Maps a Customer entity to a CustomerResponse DTO.
     * This method is package-accessible but the Customer type is internal,
     * so only code within the customer module can call this.
     */
    static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getEmail(),
            customer.getPhoneNumber(),
            customer.getStatus(),
            customer.getCreatedAt()
        );
    }
}
