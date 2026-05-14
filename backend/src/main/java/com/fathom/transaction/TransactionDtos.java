package com.fathom.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionDtos {
    public record CreateTransactionRequest(
            @NotNull UUID accountId,
            UUID categoryId,
            @NotNull LocalDate transactionDate,
            @NotNull @DecimalMin("0.0") BigDecimal amount,
            @NotNull Direction direction,
            @NotNull TransactionType transactionType,
            @NotNull TransactionSource source,
            String rawDescription,
            String merchant,
            String notes,
            Boolean internalTransfer,
            Boolean investmentTransfer,
            Boolean debtPayment,
            UUID linkedTransactionId,
            String importHash
    ) {}

    public record UpdateTransactionCategoryRequest(UUID categoryId) {}

    public record UpdateTransactionRequest(
            @NotNull LocalDate transactionDate,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
            @NotNull Direction direction,
            @NotNull TransactionType transactionType,
            @NotNull TransactionSource source,
            String rawDescription,
            String merchant,
            String notes,
            UUID categoryId,
            Boolean internalTransfer,
            Boolean investmentTransfer,
            Boolean debtPayment
    ) {}

    public record BulkCategoryUpdateRequest(java.util.List<UUID> transactionIds, UUID categoryId) {}
    public record BulkCategoryUpdateResponse(int requestedCount, int updatedCount) {}
    public record PaginatedTransactionsResponse(java.util.List<TransactionResponse> items, int page, int size, long totalItems, int totalPages) {}

    public record TransactionResponse(
            UUID id,
            UUID userId,
            UUID accountId,
            UUID categoryId,
            LocalDate transactionDate,
            BigDecimal amount,
            Direction direction,
            TransactionType transactionType,
            TransactionSource source,
            String rawDescription,
            String merchant,
            String notes,
            boolean internalTransfer,
            boolean investmentTransfer,
            boolean debtPayment,
            String importHash,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
