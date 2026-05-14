package com.fathom.rule;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRuleRepository extends JpaRepository<CategoryRule, UUID> {
    List<CategoryRule> findByUserIdOrderByPriorityAscCreatedAtAsc(UUID userId);
    List<CategoryRule> findByUserIdAndActiveTrueOrderByPriorityAscCreatedAtAsc(UUID userId);
}
