package com.fathom.upload;

import com.fathom.transaction.TransactionSource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TransactionImportDtos {
    public record ImportErrorResponse(int rowNumber, String message) {}

    public record ImportSummaryResponse(
            UUID importId, TransactionSource source, String originalFilename, ImportStatus status, int totalRows, int createdCount,
            int skippedDuplicateCount, int failedCount, List<ImportErrorResponse> errors,
            Instant createdAt, Instant updatedAt) {}
}
