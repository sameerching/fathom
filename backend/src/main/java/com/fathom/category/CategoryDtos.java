package com.fathom.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class CategoryDtos {
    public record CreateCategoryRequest(
            @NotBlank String name,
            @NotNull CategoryType categoryType,
            UUID parentCategoryId
    ) {}

    public record UpdateCategoryRequest(
            @NotBlank String name,
            @NotNull CategoryType categoryType,
            @NotNull Boolean active
    ) {}

    public record CategoryResponse(
            UUID id,
            UUID userId,
            String name,
            CategoryType categoryType,
            boolean systemDefault,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
