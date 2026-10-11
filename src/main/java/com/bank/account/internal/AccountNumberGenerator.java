package com.bank.account.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates unique account numbers.
 *
 * Format: ACC-{8-digit-number}
 * Example: ACC-00000042, ACC-12345678
 *
 * In a real banking system, account number generation would:
 * - Use a database sequence for guaranteed uniqueness
 * - Follow banking regulations for account number format
 * - Include check digits (like credit card Luhn algorithm)
 *
 * For now, we use a simple approach: try a random number and
 * retry if it already exists in the database.
 *
 * This is INTERNAL — account number generation is an implementation
 * detail of the account module.
 */
@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;

    private static final String PREFIX = "ACC";
    private static final int MAX_ATTEMPTS = 10;

    public String generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String accountNumber = PREFIX + "-" +
                String.format("%08d", ThreadLocalRandom.current().nextLong(0, 100_000_000));

            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
        throw new IllegalStateException(
            "Failed to generate unique account number after " + MAX_ATTEMPTS + " attempts"
        );
    }
}
