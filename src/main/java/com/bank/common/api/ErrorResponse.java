package com.bank.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response.
 *
 * All errors — validation failures, not found, server errors —
 * return this structure:
 *
 * {
 *   "timestamp": "2024-01-15T10:30:00Z",
 *   "status": 400,
 *   "code": "INSUFFICIENT_BALANCE",
 *   "message": "Insufficient account balance for this operation",
 *   "path": "/api/transfers",
 *   "errors": [...]        ← only present for validation failures
 * }
 *
 * The "code" field is machine-readable — the frontend can display
 * localized messages based on the code.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldError> errors
) {

    /**
     * Represents a single field-level validation error.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FieldError(
            String field,
            String message,
            Object rejectedValue
    ) {}

    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(Instant.now(), status, code, message, path, null);
    }

    public static ErrorResponse withErrors(int status, String code, String message,
                                           String path, List<FieldError> errors) {
        return new ErrorResponse(Instant.now(), status, code, message, path, errors);
    }
}
