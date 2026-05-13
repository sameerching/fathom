package com.fathom.transaction;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/api/users/{userId}/transactions")
    TransactionDtos.TransactionResponse create(@PathVariable UUID userId, @Valid @RequestBody TransactionDtos.CreateTransactionRequest request) {
        return service.create(userId, request);
    }

    @GetMapping("/api/users/{userId}/transactions")
    List<TransactionDtos.TransactionResponse> list(
            @PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) Direction direction,
            @RequestParam(required = false) TransactionSource source,
            @RequestParam(required = false) String merchant,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        return service.list(userId, from, to, accountId, categoryId, transactionType, direction, source, merchant, minAmount, maxAmount);
    }


    @PatchMapping("/api/transactions/{transactionId}/category")
    TransactionDtos.TransactionResponse updateCategory(@PathVariable UUID transactionId, @Valid @RequestBody TransactionDtos.UpdateTransactionCategoryRequest request) {
        return service.updateCategory(transactionId, request);
    }

    @GetMapping("/api/transactions/{transactionId}")
    TransactionDtos.TransactionResponse get(@PathVariable UUID transactionId) {
        return service.get(transactionId);
    }
}
