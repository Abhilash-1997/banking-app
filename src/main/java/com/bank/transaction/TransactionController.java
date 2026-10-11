package com.bank.transaction;

import com.bank.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Transaction REST Controller.
 * Exposes transfers, deposits, withdrawals, and receipt queries.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferCommand command) {

        log.info("POST /api/transactions/transfers — idempotencyKey: {}", idempotencyKey);
        TransactionResponse response = transactionService.transfer(command, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Transfer completed successfully"));
    }

    @PostMapping("/deposits")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody DepositCommand command) {

        log.info("POST /api/transactions/deposits — account: {}, amount: {}", command.accountId(), command.amount());
        TransactionResponse response = transactionService.deposit(command, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Deposit completed successfully"));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody WithdrawCommand command) {

        log.info("POST /api/transactions/withdrawals — account: {}, amount: {}", command.accountId(), command.amount());
        TransactionResponse response = transactionService.withdraw(command, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Withdrawal completed successfully"));
    }

    @GetMapping("/{referenceNumber}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getByReference(
            @PathVariable String referenceNumber) {

        log.info("GET /api/transactions/{}", referenceNumber);
        TransactionResponse response = transactionService.findByReference(referenceNumber);

        return ResponseEntity.ok(ApiResponse.success(response, "Transaction retrieved successfully"));
    }
}
