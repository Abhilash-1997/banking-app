package com.bank.ledger;

import com.bank.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Ledger REST Controller.
 * Exposes immutable ledger entries for auditing and statement generation.
 */
@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Slf4j
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<ApiResponse<List<LedgerEntryResponse>>> getByAccount(
            @PathVariable UUID accountId) {

        log.info("GET /api/ledger/accounts/{}", accountId);
        List<LedgerEntryResponse> entries = ledgerService.getEntriesByAccount(accountId);

        return ResponseEntity.ok(ApiResponse.success(entries, "Ledger entries retrieved successfully"));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<List<LedgerEntryResponse>>> getByTransaction(
            @PathVariable UUID transactionId) {

        log.info("GET /api/ledger/transactions/{}", transactionId);
        List<LedgerEntryResponse> entries = ledgerService.getEntriesByTransaction(transactionId);

        return ResponseEntity.ok(ApiResponse.success(entries, "Ledger entries retrieved successfully"));
    }
}
