package com.fathom.recurring;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
public class RecurringTransactionController {
    private final RecurringTransactionService service;
    public RecurringTransactionController(RecurringTransactionService service) { this.service = service; }
    @PostMapping("/api/users/{userId}/recurring-transactions")
    public RecurringTransactionDtos.RecurringTransactionResponse create(@PathVariable UUID userId, @RequestBody @Valid RecurringTransactionDtos.UpsertRecurringTransactionRequest request){ return service.create(userId, request); }
    @GetMapping("/api/users/{userId}/recurring-transactions")
    public List<RecurringTransactionDtos.RecurringTransactionResponse> list(@PathVariable UUID userId){ return service.list(userId); }
    @PatchMapping("/api/recurring-transactions/{id}")
    public RecurringTransactionDtos.RecurringTransactionResponse update(@PathVariable UUID id, @RequestBody @Valid RecurringTransactionDtos.UpsertRecurringTransactionRequest request){ return service.update(id, request); }
    @PatchMapping("/api/recurring-transactions/{id}/deactivate")
    public RecurringTransactionDtos.RecurringTransactionResponse deactivate(@PathVariable UUID id){ return service.deactivate(id); }
}
