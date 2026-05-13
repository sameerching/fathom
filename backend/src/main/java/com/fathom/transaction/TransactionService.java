package com.fathom.transaction;

import com.fathom.account.FinancialAccount;
import com.fathom.account.FinancialAccountService;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.user.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final UserService userService;
    private final FinancialAccountService accountService;

    public TransactionService(TransactionRepository repository, UserService userService, FinancialAccountService accountService) {
        this.repository = repository;
        this.userService = userService;
        this.accountService = accountService;
    }

    public TransactionDtos.TransactionResponse create(UUID userId, TransactionDtos.CreateTransactionRequest r) {
        userService.getEntity(userId);
        FinancialAccount account = accountService.getEntity(r.accountId());
        if (!account.getUserId().equals(userId)) throw new ResourceNotFoundException("Account not found");
        Transaction t = new Transaction();
        t.setUserId(userId); t.setAccountId(r.accountId()); t.setCategoryId(r.categoryId()); t.setTransactionDate(r.transactionDate());
        t.setAmount(r.amount()); t.setDirection(r.direction()); t.setTransactionType(r.transactionType()); t.setSource(r.source());
        t.setRawDescription(r.rawDescription()); t.setMerchant(r.merchant()); t.setNotes(r.notes());
        t.setInternalTransfer(Boolean.TRUE.equals(r.internalTransfer())); t.setInvestmentTransfer(Boolean.TRUE.equals(r.investmentTransfer())); t.setDebtPayment(Boolean.TRUE.equals(r.debtPayment()));
        t.setLinkedTransactionId(r.linkedTransactionId()); t.setImportHash(r.importHash());
        return toResponse(repository.save(t));
    }

    public List<TransactionDtos.TransactionResponse> list(UUID userId, LocalDate from, LocalDate to) {
        userService.getEntity(userId);
        if (from != null && to != null) return repository.findByUserIdAndTransactionDateBetween(userId, from, to).stream().map(this::toResponse).toList();
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public TransactionDtos.TransactionResponse get(UUID id) { return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"))); }

    private TransactionDtos.TransactionResponse toResponse(Transaction t){ return new TransactionDtos.TransactionResponse(t.getId(),t.getUserId(),t.getAccountId(),t.getCategoryId(),t.getTransactionDate(),t.getAmount(),t.getDirection(),t.getTransactionType(),t.getSource(),t.getMerchant(),t.getCreatedAt(),t.getUpdatedAt()); }
}
