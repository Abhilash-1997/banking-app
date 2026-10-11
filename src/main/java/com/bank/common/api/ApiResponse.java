package com.bank.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standardized API response wrapper.
 *
 * Every successful API response uses this wrapper so the frontend
 * always receives a consistent structure:
 *
 * {
 *   "success": true,
 *   "message": "Customer registered successfully",
 *   "data": { ... },
 *   "timestamp": "2024-01-15T10:30:00Z"
 * }
 *
 * Why wrap responses?
 * - Frontend knows exactly where to find data
 * - Easy to add metadata (pagination, trace ID) later
 * - Consistent experience across all endpoints
 *
 * @param <T> the type of data being returned
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {

    /**
     * Create a successful response with data and a message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }

    /**
     * Create a successful response with only a message (no data body).
     * Useful for operations like DELETE.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, Instant.now());
    }
}
