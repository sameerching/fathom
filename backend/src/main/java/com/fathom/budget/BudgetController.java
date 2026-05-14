package com.fathom.budget;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
public class BudgetController {
    private final BudgetService service;
    public BudgetController(BudgetService service){this.service=service;}
    @PostMapping("/api/users/{userId}/budgets") BudgetDtos.BudgetResponse create(@PathVariable UUID userId,@Valid @RequestBody BudgetDtos.UpsertBudgetRequest request){ return service.create(userId,request); }
    @GetMapping("/api/users/{userId}/budgets") List<BudgetDtos.BudgetResponse> list(@PathVariable UUID userId,@RequestParam(required = false) String month){ return service.list(userId,month); }
    @PatchMapping("/api/budgets/{budgetId}") BudgetDtos.BudgetResponse update(@PathVariable UUID budgetId,@Valid @RequestBody BudgetDtos.UpsertBudgetRequest request){ return service.update(budgetId,request); }
    @PatchMapping("/api/budgets/{budgetId}/deactivate") BudgetDtos.BudgetResponse deactivate(@PathVariable UUID budgetId){ return service.deactivate(budgetId); }
    @GetMapping("/api/users/{userId}/budget-summary") List<BudgetDtos.BudgetSummaryItem> summary(@PathVariable UUID userId,@RequestParam String month){ return service.summary(userId,month); }
}
