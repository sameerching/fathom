package com.fathom.planning;

import java.math.BigDecimal;
import java.util.UUID;

public class PlanningDtos {
    public record MonthlyPlanningSummaryResponse(UUID userId, String month, BigDecimal plannedIncome, BigDecimal actualIncome, BigDecimal incomeVariance,
                                                 BigDecimal plannedExpenses, BigDecimal actualExpenses, BigDecimal expensesVariance,
                                                 BigDecimal plannedInvestments, BigDecimal actualInvestments, BigDecimal investmentsVariance,
                                                 BigDecimal plannedLiabilityPayments, BigDecimal actualLiabilityPayments, BigDecimal liabilityPaymentsVariance,
                                                 BigDecimal plannedNetCashFlow, BigDecimal actualNetCashFlow, BigDecimal netCashFlowVariance) {}
}
