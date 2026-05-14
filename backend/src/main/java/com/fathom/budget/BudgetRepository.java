package com.fathom.budget;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByUserIdOrderByMonthDescNameAsc(UUID userId);
    List<Budget> findByUserIdAndMonthOrderByMonthDescNameAsc(UUID userId, String month);
    List<Budget> findByUserIdAndMonthAndActiveTrueOrderByNameAsc(UUID userId, String month);
}
