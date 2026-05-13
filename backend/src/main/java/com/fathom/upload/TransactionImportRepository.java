package com.fathom.upload;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionImportRepository extends JpaRepository<TransactionImport, UUID> {
    List<TransactionImport> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
