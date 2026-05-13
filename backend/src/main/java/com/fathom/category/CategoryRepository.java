package com.fathom.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    List<Category> findByUserIdAndActiveTrue(UUID userId);
    List<Category> findBySystemDefaultTrueAndActiveTrue();
    Optional<Category> findByUserIdAndNameIgnoreCaseAndActiveTrue(UUID userId, String name);
    Optional<Category> findBySystemDefaultTrueAndNameIgnoreCaseAndActiveTrue(String name);
    boolean existsByUserIdAndCategoryTypeAndNameIgnoreCase(UUID userId, CategoryType categoryType, String name);
    Optional<Category> findByUserIdAndCategoryTypeAndNameIgnoreCase(UUID userId, CategoryType categoryType, String name);
}
