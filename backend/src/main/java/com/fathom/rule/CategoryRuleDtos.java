package com.fathom.rule;

import com.fathom.transaction.Direction;
import com.fathom.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CategoryRuleDtos {
    public record UpsertRuleRequest(
            @NotBlank String name,
            Integer priority,
            @NotNull RuleField ruleField,
            @NotNull MatchOperator matchOperator,
            @NotBlank String matchValue,
            @NotNull UUID categoryId,
            TransactionType transactionType,
            Direction direction,
            Boolean active) {}

    public record RuleResponse(UUID id, UUID userId, String name, int priority, RuleField ruleField, MatchOperator matchOperator,
                               String matchValue, UUID categoryId, TransactionType transactionType, Direction direction,
                               boolean active, java.time.Instant createdAt, java.time.Instant updatedAt) {}

    public record ApplyRulesResponse(int matchedCount, int updatedCount, int skippedCount) {}
}
