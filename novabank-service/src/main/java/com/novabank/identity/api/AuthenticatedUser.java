package com.novabank.identity.api;

/**
 * Snapshot of the authenticated user taken from the IdP JWT.
 * Persistence of {@code user_profile} is the next identity increment.
 */
public record AuthenticatedUser(String subject, String preferredUsername) {
}
