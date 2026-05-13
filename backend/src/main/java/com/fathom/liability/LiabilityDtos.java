package com.fathom.liability;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class LiabilityDtos {
    public record CreateLiabilityRequest(
            @NotNull LiabilityType liabilityType,
            @NotBlank String name,
            String lender,
            String currency,
            @DecimalMin("0.0") BigDecimal principalAmount,
            @NotNull @DecimalMin("0.0") BigDecimal outstandingAmount,
            BigDecimal interestRate,
            BigDecimal emiAmount,
            LocalDate startDate,
            LocalDate endDate
    ) {}

    public record LiabilityResponse(
            UUID id,
            UUID userId,
            LiabilityType liabilityType,
            String name,
            String lender,
            String currency,
            BigDecimal principalAmount,
            BigDecimal outstandingAmount,
            BigDecimal interestRate,
            BigDecimal emiAmount,
            LocalDate startDate,
            LocalDate endDate,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
