package com.fathom.upload;

import com.fathom.common.BaseEntity;
import com.fathom.transaction.TransactionSource;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "transaction_imports")
public class TransactionImport extends BaseEntity {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "account_id", nullable = false) private UUID accountId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TransactionSource source;
    @Column(name = "original_filename", nullable = false) private String originalFilename;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ImportStatus status;
    @Column(name = "total_rows", nullable = false) private int totalRows;
    @Column(name = "created_count", nullable = false) private int createdCount;
    @Column(name = "skipped_duplicate_count", nullable = false) private int skippedDuplicateCount;
    @Column(name = "failed_count", nullable = false) private int failedCount;
    @PrePersist void init(){ if (id == null) id = UUID.randomUUID(); }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getAccountId(){return accountId;} public TransactionSource getSource(){return source;} public String getOriginalFilename(){return originalFilename;} public ImportStatus getStatus(){return status;} public int getTotalRows(){return totalRows;} public int getCreatedCount(){return createdCount;} public int getSkippedDuplicateCount(){return skippedDuplicateCount;} public int getFailedCount(){return failedCount;}
    public void setUserId(UUID v){userId=v;} public void setAccountId(UUID v){accountId=v;} public void setSource(TransactionSource v){source=v;} public void setOriginalFilename(String v){originalFilename=v;} public void setStatus(ImportStatus v){status=v;} public void setTotalRows(int v){totalRows=v;} public void setCreatedCount(int v){createdCount=v;} public void setSkippedDuplicateCount(int v){skippedDuplicateCount=v;} public void setFailedCount(int v){failedCount=v;}
}
