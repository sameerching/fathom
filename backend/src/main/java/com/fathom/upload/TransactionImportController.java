package com.fathom.upload;

import com.fathom.transaction.TransactionSource;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TransactionImportController {
    private final TransactionImportService service;
    public TransactionImportController(TransactionImportService service){this.service=service;}

    @PostMapping("/api/users/{userId}/accounts/{accountId}/transaction-imports")
    TransactionImportDtos.ImportSummaryResponse upload(@PathVariable UUID userId, @PathVariable UUID accountId,
                                                       @RequestParam("file") MultipartFile file,
                                                       @RequestParam(defaultValue = "MANUAL") String source){
        return service.importCsv(userId, accountId, file, parseAllowedSource(source));
    }

    private TransactionSource parseAllowedSource(String source){
        TransactionSource parsed = TransactionSource.valueOf(source.toUpperCase());
        if (parsed != TransactionSource.MANUAL && parsed != TransactionSource.BANK_STATEMENT && parsed != TransactionSource.CREDIT_CARD_STATEMENT) {
            throw new IllegalArgumentException("Unsupported source");
        }
        return parsed;
    }

    @GetMapping("/api/users/{userId}/transaction-imports")
    List<TransactionImportDtos.ImportSummaryResponse> list(@PathVariable UUID userId){ return service.listByUser(userId); }

    @GetMapping("/api/transaction-imports/{importId}")
    TransactionImportDtos.ImportSummaryResponse get(@PathVariable UUID importId){ return service.get(importId); }
}
