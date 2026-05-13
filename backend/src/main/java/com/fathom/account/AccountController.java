package com.fathom.account;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    private final FinancialAccountService service;

    public AccountController(FinancialAccountService service) {
        this.service = service;
    }

    @PostMapping("/api/users/{userId}/accounts")
    AccountDtos.FinancialAccountResponse create(
            @PathVariable UUID userId,
            @Valid @RequestBody AccountDtos.CreateFinancialAccountRequest request
    ) {
        return service.create(userId, request);
    }

    @GetMapping("/api/users/{userId}/accounts")
    List<AccountDtos.FinancialAccountResponse> list(@PathVariable UUID userId) {
        return service.listByUser(userId);
    }

    @GetMapping("/api/accounts/{accountId}")
    AccountDtos.FinancialAccountResponse get(@PathVariable UUID accountId) {
        return service.get(accountId);
    }
}
