package com.fathom.dashboard;

import com.fathom.transaction.TransactionType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/users/{userId}/dashboard/monthly-summary")
    DashboardDtos.MonthlySummaryResponse monthlySummary(
            @PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return dashboardService.monthlySummary(userId, month);
    }

    @GetMapping("/api/users/{userId}/dashboard/category-breakdown")
    List<DashboardDtos.CategoryBreakdownItem> categoryBreakdown(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "EXPENSE") TransactionType type) {
        return dashboardService.categoryBreakdown(userId, from, to, type);
    }

    @GetMapping("/api/users/{userId}/dashboard/net-worth")
    DashboardDtos.NetWorthSummaryResponse netWorth(@PathVariable UUID userId) {
        return dashboardService.netWorth(userId);
    }
}
