package com.bank.customer.internal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * Customer JPA Entity — INTERNAL to the customer module.
 *
 * The 'internal' package is the key Spring Modulith mechanism.
 * Spring Modulith's architecture verification will FAIL if any class
 * outside the 'customer' module imports this class.
 *
 * This means:
 * - AccountService CANNOT use Customer directly
 * - TransactionService CANNOT use Customer directly
 * - They must call CustomerService (the public module API) instead
 *
 * Why is this good?
 * If we ever change the Customer entity's structure (add a field,
 * rename a column, split into two tables), only the customer module
 * is affected. Other modules are protected by the boundary.
 *
 * Design Notes:
 * - No public setters — state changes go through domain methods
 * - @Version for optimistic locking
 * - Static factory method instead of public constructor
 */
@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA requires no-arg constructor
@ToString(exclude = {}) // Safe to log — no sensitive fields
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    /**
     * Optimistic locking version.
     *
     * How it works:
     * 1. Thread A reads Customer with version=1
     * 2. Thread B reads Customer with version=1
     * 3. Thread A saves → version becomes 2
     * 4. Thread B tries to save with version=1 → FAILS with OptimisticLockException
     *
     * This prevents "lost update" bugs where two concurrent requests
     * overwrite each other's changes.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Static factory method — preferred over public constructor.
     * Centralizes all creation logic and enforces invariants.
     *
     * A newly created Customer is always PENDING (not yet verified).
     */
    public static Customer create(String firstName, String lastName,
                                   String email, String phoneNumber) {
        Customer customer = new Customer();
        customer.firstName = firstName.trim();
        customer.lastName = lastName.trim();
        customer.email = email.toLowerCase().trim();
        customer.phoneNumber = phoneNumber;
        customer.status = CustomerStatus.PENDING;
        customer.createdAt = Instant.now();
        customer.updatedAt = Instant.now();
        customer.createdBy = "SYSTEM";
        return customer;
    }

    /**
     * Activate a PENDING customer.
     * In a real system this would happen after email verification.
     */
    public void activate() {
        if (this.status != CustomerStatus.PENDING) {
            throw new IllegalStateException(
                "Cannot activate customer in status: " + this.status
            );
        }
        this.status = CustomerStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
