package com.bank.customer.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Customer data access — INTERNAL to the customer module.
 *
 * This repository is in the 'internal' package.
 * Spring Modulith will reject any attempt by another module
 * to inject or import this interface.
 *
 * The Account module, for example, CANNOT do:
 *   @Autowired CustomerRepository customerRepository; // ← FORBIDDEN
 *
 * It must instead call:
 *   customerService.findById(id); // ← CORRECT — through the module API
 *
 * This discipline ensures that the customer module controls all access
 * to its data. If the data structure changes, only CustomerRepository
 * and its callers within the customer module need to be updated.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}
