package com.fathom.investment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class InvestmentDtos {
    public record CreateInvestmentHoldingRequest(
            @NotNull AssetType assetType,
            @NotBlank String name,
            String provider,
            String symbol,
            String currency,
            @DecimalMin("0.0") BigDecimal investedAmount,
            @DecimalMin("0.0") BigDecimal currentValue,
            LocalDate asOfDate
    ) {}

    public record InvestmentHoldingResponse(
            UUID id,
            UUID userId,
            AssetType assetType,
            String name,
            String provider,
            String symbol,
            String currency,
            BigDecimal investedAmount,
            BigDecimal currentValue,
            LocalDate asOfDate,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
