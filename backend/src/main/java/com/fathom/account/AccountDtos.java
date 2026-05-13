package com.fathom.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class AccountDtos {
    public record CreateFinancialAccountRequest(
            @NotBlank String name,
            String institutionName,
            @NotNull AccountType accountType,
            String currency,
            String maskedIdentifier
    ) {}

    public record FinancialAccountResponse(
            UUID id,
            UUID userId,
            String name,
            String institutionName,
            AccountType accountType,
            String currency,
            String maskedIdentifier,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
