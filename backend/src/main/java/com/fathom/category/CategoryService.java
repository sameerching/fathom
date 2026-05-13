package com.fathom.category;

import com.fathom.common.DuplicateResourceException;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final UserService userService;

    public CategoryService(CategoryRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<Category> getSystem() { return repository.findBySystemDefaultTrueAndActiveTrue(); }

    public List<Category> getByUser(UUID userId) {
        userService.getEntity(userId);
        return repository.findByUserIdAndActiveTrue(userId);
    }

    public Category createUserCategory(UUID userId, CategoryDtos.CreateCategoryRequest request) {
        userService.getEntity(userId);
        if (repository.existsByUserIdAndCategoryTypeAndNameIgnoreCase(userId, request.categoryType(), request.name())) {
            throw new DuplicateResourceException("Category already exists");
        }

        if (request.parentCategoryId() != null) {
            Category parent = repository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            if (!parent.isSystemDefault() && !userId.equals(parent.getUserId())) {
                throw new IllegalArgumentException("Parent category must belong to same user or be system default");
            }
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setName(request.name());
        category.setCategoryType(request.categoryType());
        category.setParentCategoryId(request.parentCategoryId());
        category.setSystemDefault(false);
        category.setActive(true);
        return repository.save(category);
    }

    public Category updateCategory(UUID categoryId, CategoryDtos.UpdateCategoryRequest request) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.isSystemDefault()) {
            throw new IllegalArgumentException("System default categories cannot be edited");
        }

        if (repository.findByUserIdAndCategoryTypeAndNameIgnoreCase(category.getUserId(), request.categoryType(), request.name())
                .filter(existing -> !existing.getId().equals(category.getId()))
                .isPresent()) {
            throw new DuplicateResourceException("Category already exists");
        }

        category.setName(request.name());
        category.setCategoryType(request.categoryType());
        category.setActive(request.active());
        return repository.save(category);
    }

    public Category resolveCategoryForUser(UUID userId, UUID categoryId) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!category.isSystemDefault() && !userId.equals(category.getUserId())) {
            throw new IllegalArgumentException("Category does not belong to transaction user");
        }
        return category;
    }
}
