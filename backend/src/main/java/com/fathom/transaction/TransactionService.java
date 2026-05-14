package com.fathom.transaction;

import com.fathom.account.FinancialAccount;
import com.fathom.account.FinancialAccountService;
import com.fathom.category.CategoryService;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.rule.CategoryRuleService;
import com.fathom.user.UserService;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final UserService userService;
    private final FinancialAccountService accountService;
    private final CategoryService categoryService;
    private final CategoryRuleService categoryRuleService;

    public TransactionService(TransactionRepository repository, UserService userService, FinancialAccountService accountService, CategoryService categoryService, CategoryRuleService categoryRuleService) {
        this.repository = repository;
        this.userService = userService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.categoryRuleService = categoryRuleService;
    }

    public TransactionDtos.TransactionResponse create(UUID userId, TransactionDtos.CreateTransactionRequest r) {
        userService.getEntity(userId);
        FinancialAccount account = accountService.getEntity(r.accountId());
        if (!account.getUserId().equals(userId)) throw new ResourceNotFoundException("Account not found");
        if (r.categoryId() != null) {
            categoryService.resolveCategoryForUser(userId, r.categoryId());
        }

        Transaction t = new Transaction();
        t.setUserId(userId); t.setAccountId(r.accountId()); t.setCategoryId(r.categoryId()); t.setTransactionDate(r.transactionDate());
        t.setAmount(r.amount()); t.setDirection(r.direction()); t.setTransactionType(r.transactionType()); t.setSource(r.source());
        t.setRawDescription(r.rawDescription()); t.setMerchant(r.merchant()); t.setNotes(r.notes());
        if (t.getCategoryId() == null) categoryRuleService.matchCategoryId(userId, t).ifPresent(t::setCategoryId);
        t.setInternalTransfer(Boolean.TRUE.equals(r.internalTransfer())); t.setInvestmentTransfer(Boolean.TRUE.equals(r.investmentTransfer())); t.setDebtPayment(Boolean.TRUE.equals(r.debtPayment()));
        t.setLinkedTransactionId(r.linkedTransactionId()); t.setImportHash(r.importHash());
        return toResponse(repository.save(t));
    }

    public List<TransactionDtos.TransactionResponse> list(UUID userId, LocalDate from, LocalDate to, UUID accountId, UUID categoryId,
                                                          TransactionType transactionType, Direction direction, TransactionSource source,
                                                          String merchant, BigDecimal minAmount, BigDecimal maxAmount) {
        userService.getEntity(userId);
        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), to));
            if (accountId != null) predicates.add(cb.equal(root.get("accountId"), accountId));
            if (categoryId != null) predicates.add(cb.equal(root.get("categoryId"), categoryId));
            if (transactionType != null) predicates.add(cb.equal(root.get("transactionType"), transactionType));
            if (direction != null) predicates.add(cb.equal(root.get("direction"), direction));
            if (source != null) predicates.add(cb.equal(root.get("source"), source));
            if (merchant != null && !merchant.isBlank()) predicates.add(cb.like(cb.lower(root.get("merchant")), "%" + merchant.toLowerCase() + "%"));
            if (minAmount != null) predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            if (maxAmount != null) predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, Sort.by(Sort.Order.desc("transactionDate"), Sort.Order.desc("createdAt"))).stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionDtos.TransactionResponse get(UUID id) { return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"))); }

    public TransactionDtos.TransactionResponse updateCategory(UUID transactionId, TransactionDtos.UpdateTransactionCategoryRequest request) {
        Transaction transaction = repository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (request.categoryId() == null) {
            transaction.setCategoryId(null);
        } else {
            categoryService.resolveCategoryForUser(transaction.getUserId(), request.categoryId());
            transaction.setCategoryId(request.categoryId());
        }

        return toResponse(repository.save(transaction));
    }

    private TransactionDtos.TransactionResponse toResponse(Transaction t){
        return new TransactionDtos.TransactionResponse(t.getId(),t.getUserId(),t.getAccountId(),t.getCategoryId(),t.getTransactionDate(),
                t.getAmount(),t.getDirection(),t.getTransactionType(),t.getSource(),t.getRawDescription(),t.getMerchant(),t.getNotes(),
                t.isInternalTransfer(),t.isInvestmentTransfer(),t.isDebtPayment(),t.getImportHash(),t.getCreatedAt(),t.getUpdatedAt());
    }
}
