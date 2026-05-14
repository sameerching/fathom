package com.fathom.recurring;

import com.fathom.transaction.Direction;
import com.fathom.transaction.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class RecurringTransactionDtos {
    public record UpsertRecurringTransactionRequest(UUID accountId, UUID categoryId, @NotBlank String name, @NotNull @DecimalMin(value = "0.0001") BigDecimal amount,
                                                    @NotNull Direction direction, @NotNull TransactionType transactionType, @NotNull RecurrenceFrequency frequency,
                                                    @Min(1) @Max(31) Integer dayOfMonth, @NotNull LocalDate startDate, LocalDate endDate, Boolean active, String notes) {}
    public record RecurringTransactionResponse(UUID id, UUID userId, UUID accountId, UUID categoryId, String name, BigDecimal amount, Direction direction,
                                               TransactionType transactionType, RecurrenceFrequency frequency, Integer dayOfMonth, LocalDate startDate, LocalDate endDate,
                                               boolean active, String notes, Instant createdAt, Instant updatedAt) {}
}
