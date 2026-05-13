package com.fathom.upload;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TransactionImportDtos {
    public record ImportErrorResponse(int rowNumber, String message) {}

    public record ImportSummaryResponse(
            UUID importId, ImportStatus status, int totalRows, int createdCount,
            int skippedDuplicateCount, int failedCount, List<ImportErrorResponse> errors,
            Instant createdAt, Instant updatedAt) {}
}
