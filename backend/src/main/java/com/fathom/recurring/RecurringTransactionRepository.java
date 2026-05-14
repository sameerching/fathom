package com.fathom.recurring;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, UUID> {
    List<RecurringTransaction> findByUserId(UUID userId, Sort sort);
    List<RecurringTransaction> findByUserIdAndActiveTrue(UUID userId);
}
