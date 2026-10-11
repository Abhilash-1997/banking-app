package com.bank.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Command object for customer registration.
 *
 * Why "Command"?
 * We distinguish between:
 * - Commands: intent to change state (RegisterCustomerCommand, CreateAccountCommand)
 * - Queries: requests for data (FindCustomerQuery)
 * - Events: something that happened (CustomerRegisteredEvent)
 *
 * This naming comes from CQRS thinking, but we don't need full CQRS
 * to benefit from clear naming conventions.
 *
 * This is also the DTO that the controller receives from the HTTP request body.
 * Bean validation annotations (@NotBlank, @Email, etc.) run before the
 * service layer is called.
 *
 * Using a Java record: immutable by default, compact, no boilerplate.
 */
public record RegisterCustomerCommand(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phoneNumber
) {}
