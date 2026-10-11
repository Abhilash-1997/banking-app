package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Banking Application Entry Point
 *
 * This is the root of our Spring Boot application.
 *
 * Spring Modulith automatically detects application modules by scanning
 * all direct sub-packages of this class's package (com.bank).
 *
 * So:
 *   com.bank.customer     → Module "customer"
 *   com.bank.account      → Module "account"
 *   com.bank.common       → Module "common"
 *
 * No special annotation is needed for Spring Modulith to work.
 * The package structure IS the module declaration.
 */
@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
