package com.fathom.transaction;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
    private final TransactionService service;
    public TransactionController(TransactionService service) { this.service = service; }

    @PostMapping("/api/users/{userId}/transactions")
    TransactionDtos.TransactionResponse create(@PathVariable UUID userId, @Valid @RequestBody TransactionDtos.CreateTransactionRequest request) { return service.create(userId, request); }

    @GetMapping("/api/users/{userId}/transactions")
    TransactionDtos.PaginatedTransactionsResponse list(@PathVariable UUID userId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, @RequestParam(required = false) UUID accountId, @RequestParam(required = false) UUID categoryId,
        @RequestParam(required = false) TransactionType transactionType, @RequestParam(required = false) Direction direction, @RequestParam(required = false) TransactionSource source,
        @RequestParam(required = false) String merchant, @RequestParam(required = false) BigDecimal minAmount, @RequestParam(required = false) BigDecimal maxAmount,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return service.list(userId, from, to, accountId, categoryId, transactionType, direction, source, merchant, minAmount, maxAmount, page, size);
    }

    @PatchMapping("/api/transactions/{transactionId}/category")
    TransactionDtos.TransactionResponse updateCategory(@PathVariable UUID transactionId, @Valid @RequestBody TransactionDtos.UpdateTransactionCategoryRequest request) { return service.updateCategory(transactionId, request); }
    @PatchMapping("/api/transactions/{transactionId}")
    TransactionDtos.TransactionResponse update(@PathVariable UUID transactionId, @Valid @RequestBody TransactionDtos.UpdateTransactionRequest request){ return service.update(transactionId, request);}    
    @DeleteMapping("/api/transactions/{transactionId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID transactionId){ service.delete(transactionId);}    
    @PatchMapping("/api/users/{userId}/transactions/bulk-category")
    TransactionDtos.BulkCategoryUpdateResponse bulkUpdate(@PathVariable UUID userId, @RequestBody TransactionDtos.BulkCategoryUpdateRequest request){ return service.bulkUpdateCategory(userId, request);}    
    @GetMapping("/api/transactions/{transactionId}")
    TransactionDtos.TransactionResponse get(@PathVariable UUID transactionId) { return service.get(transactionId); }
}
