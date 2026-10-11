package com.bank.common.exception;

/**
 * Thrown when a requested resource does not exist.
 * Maps to HTTP 404 Not Found.
 *
 * Example: GET /api/customers/{id} where id doesn't exist.
 */
public class ResourceNotFoundException extends BankingException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(
            "RESOURCE_NOT_FOUND",
            String.format("%s not found with identifier: %s", resourceName, identifier)
        );
    }
}
