package com.bank.common.exception;

/**
 * Base exception for all banking domain errors.
 *
 * Design Decision: We use a hierarchy of typed exceptions rather than
 * throwing generic RuntimeException everywhere. This gives us:
 * 1. Type-safe catch blocks in tests
 * 2. Centralized error handling in @RestControllerAdvice that can map
 *    each exception type to the correct HTTP status code
 * 3. Clear intent — reading the code tells you what can go wrong
 *
 * All custom exceptions extend this class.
 */
public class BankingException extends RuntimeException {

    private final String errorCode;

    public BankingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BankingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
