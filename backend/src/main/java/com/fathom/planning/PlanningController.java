package com.fathom.planning;

import java.time.YearMonth;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlanningController {
    private final PlanningService service;
    public PlanningController(PlanningService service) { this.service = service; }
    @GetMapping("/api/users/{userId}/planning/monthly-summary")
    public PlanningDtos.MonthlyPlanningSummaryResponse monthlySummary(@PathVariable UUID userId, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month){ return service.monthlySummary(userId, month); }
}
