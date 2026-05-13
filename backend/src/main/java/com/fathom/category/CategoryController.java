package com.fathom.category;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/api/categories/system")
    List<CategoryResponse> system() { return service.getSystem().stream().map(this::map).toList(); }

    @GetMapping("/api/users/{userId}/categories")
    List<CategoryResponse> byUser(@PathVariable UUID userId) { return service.getByUser(userId).stream().map(this::map).toList(); }

    private CategoryResponse map(Category c) { return new CategoryResponse(c.getId(), c.getUserId(), c.getName(), c.getCategoryType(), c.isSystemDefault(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt()); }

    record CategoryResponse(UUID id, UUID userId, String name, CategoryType categoryType, boolean systemDefault,
                            boolean active, Instant createdAt, Instant updatedAt) {}
}
