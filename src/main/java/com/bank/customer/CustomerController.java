package com.bank.customer;

import com.bank.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Customer REST Controller.
 *
 * Responsibilities:
 * - Parse HTTP requests
 * - Validate input (via @Valid)
 * - Delegate to CustomerService
 * - Wrap response in ApiResponse
 * - Set appropriate HTTP status codes
 *
 * The controller does NOT contain business logic.
 * It's a thin HTTP adapter layer.
 *
 * Why is the controller in the root customer package (not internal)?
 * Controllers are entry points — they accept external HTTP requests.
 * They are inherently public-facing, so they belong at the module root.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * POST /api/customers
     * Register a new customer.
     *
     * HTTP 201 Created: The resource was created successfully.
     * We return the created resource in the body so the client
     * immediately has the new customer's ID and details.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> register(
            @Valid @RequestBody RegisterCustomerCommand command) {

        log.info("POST /api/customers — registering customer: {}", command.email());
        CustomerResponse response = customerService.register(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Customer registered successfully"));
    }

    /**
     * GET /api/customers/{id}
     * Retrieve a customer by their UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable UUID id) {
        log.info("GET /api/customers/{}", id);
        CustomerResponse response = customerService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer retrieved successfully"));
    }

    /**
     * PUT /api/customers/{id}/activate
     * Activate a pending customer.
     *
     * In a real system, activation would happen via an email verification
     * link with a token. For now, this is a direct endpoint for
     * development/testing.
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CustomerResponse>> activate(@PathVariable UUID id) {
        log.info("PUT /api/customers/{}/activate", id);
        CustomerResponse response = customerService.activate(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer activated successfully"));
    }
}
