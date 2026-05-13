package com.fathom.dashboard;

import com.fathom.transaction.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public class DashboardDtos {
    public record MonthlySummaryResponse(
            UUID userId, String month, BigDecimal income, BigDecimal expenses, BigDecimal investments,
            BigDecimal liabilityPayments, BigDecimal transfers, BigDecimal refunds, BigDecimal adjustments,
            BigDecimal netCashFlow, BigDecimal savingsRate) {}

    public record CategoryBreakdownItem(
            UUID categoryId, String categoryName, TransactionType transactionType, BigDecimal amount, long transactionCount) {}

    public record NetWorthSummaryResponse(UUID userId, BigDecimal totalAssets, BigDecimal totalLiabilities, BigDecimal netWorth) {}
}
