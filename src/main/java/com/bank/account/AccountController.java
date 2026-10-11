package com.bank.account;

import com.bank.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Account REST Controller.
 *
 * Thin HTTP adapter — delegates all logic to AccountService.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * POST /api/accounts
     * Create a new bank account.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> create(
            @Valid @RequestBody CreateAccountCommand command) {

        log.info("POST /api/accounts — type: {}, customer: {}",
            command.accountType(), command.customerId());

        AccountResponse response = accountService.createAccount(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Account created successfully"));
    }

    /**
     * GET /api/accounts?customerId={uuid}
     * Retrieve all accounts for a customer.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getByCustomer(
            @RequestParam UUID customerId) {

        log.info("GET /api/accounts?customerId={}", customerId);
        List<AccountResponse> accounts = accountService.findByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(accounts, "Accounts retrieved successfully"));
    }

    /**
     * GET /api/accounts/{id}
     * Retrieve a specific account.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getById(@PathVariable UUID id) {
        log.info("GET /api/accounts/{}", id);
        AccountResponse response = accountService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Account retrieved successfully"));
    }

    /**
     * PUT /api/accounts/{id}/activate
     * Activate a pending account.
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AccountResponse>> activate(@PathVariable UUID id) {
        log.info("PUT /api/accounts/{}/activate", id);
        AccountResponse response = accountService.activate(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Account activated successfully"));
    }
}
