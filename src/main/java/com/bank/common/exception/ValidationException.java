package com.bank.common.exception;

/**
 * Thrown when business validation fails.
 * Maps to HTTP 400 Bad Request.
 *
 * Distinct from Spring's MethodArgumentNotValidException (which handles
 * @Valid failures). This is for domain-level business rule violations
 * that can only be checked after hitting the database.
 *
 * Example: "Email already registered", "Account number already exists"
 */
public class ValidationException extends BankingException {

    public ValidationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
