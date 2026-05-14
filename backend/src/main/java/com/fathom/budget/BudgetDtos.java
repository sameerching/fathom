package com.fathom.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class BudgetDtos {
    public enum BudgetStatus { UNDER_BUDGET, OVER_BUDGET }
    public record UpsertBudgetRequest(@NotBlank String name, @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}") String month, UUID categoryId,
                                      @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount, Boolean active, String notes) {}
    public record BudgetResponse(UUID id, UUID userId, UUID categoryId, String name, String month, BigDecimal amount, boolean active, String notes, Instant createdAt, Instant updatedAt) {}
    public record BudgetSummaryItem(UUID budgetId, String name, String month, UUID categoryId, String categoryName, BigDecimal budgetAmount, BigDecimal actualAmount, BigDecimal remainingAmount, BigDecimal usagePercent, BudgetStatus status) {}
}
