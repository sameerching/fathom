package com.fathom.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(UUID userId);
    List<Transaction> findByUserIdAndTransactionDateBetween(UUID userId, LocalDate from, LocalDate to);
    boolean existsByUserIdAndImportHash(UUID userId, String importHash);
}
