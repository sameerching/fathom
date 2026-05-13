package com.fathom.category;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/api/categories/system")
    List<CategoryDtos.CategoryResponse> system() { return service.getSystem().stream().map(this::map).toList(); }

    @GetMapping("/api/users/{userId}/categories")
    List<CategoryDtos.CategoryResponse> byUser(@PathVariable UUID userId) { return service.getByUser(userId).stream().map(this::map).toList(); }

    @PostMapping("/api/users/{userId}/categories")
    CategoryDtos.CategoryResponse create(@PathVariable UUID userId, @Valid @RequestBody CategoryDtos.CreateCategoryRequest request) {
        return map(service.createUserCategory(userId, request));
    }

    @PatchMapping("/api/categories/{categoryId}")
    CategoryDtos.CategoryResponse update(@PathVariable UUID categoryId, @Valid @RequestBody CategoryDtos.UpdateCategoryRequest request) {
        return map(service.updateCategory(categoryId, request));
    }

    private CategoryDtos.CategoryResponse map(Category c) {
        return new CategoryDtos.CategoryResponse(c.getId(), c.getUserId(), c.getName(), c.getCategoryType(), c.isSystemDefault(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
