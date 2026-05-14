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
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final TransactionRepository repository; private final UserService userService; private final FinancialAccountService accountService; private final CategoryService categoryService; private final CategoryRuleService categoryRuleService;
    public TransactionService(TransactionRepository repository, UserService userService, FinancialAccountService accountService, CategoryService categoryService, CategoryRuleService categoryRuleService) {this.repository = repository; this.userService = userService; this.accountService = accountService; this.categoryService = categoryService; this.categoryRuleService = categoryRuleService;}
    public TransactionDtos.TransactionResponse create(UUID userId, TransactionDtos.CreateTransactionRequest r) {userService.getEntity(userId); FinancialAccount account = accountService.getEntity(r.accountId()); if (!account.getUserId().equals(userId)) throw new ResourceNotFoundException("Account not found"); if (r.categoryId() != null) categoryService.resolveCategoryForUser(userId, r.categoryId()); Transaction t = new Transaction(); t.setUserId(userId); t.setAccountId(r.accountId()); applyUpdate(t, r.transactionDate(), r.amount(), r.direction(), r.transactionType(), r.source(), r.rawDescription(), r.merchant(), r.notes(), r.categoryId(), r.internalTransfer(), r.investmentTransfer(), r.debtPayment()); if (t.getCategoryId() == null) categoryRuleService.matchCategoryId(userId, t).ifPresent(t::setCategoryId); t.setLinkedTransactionId(r.linkedTransactionId()); t.setImportHash(r.importHash()); return toResponse(repository.save(t));}

    public TransactionDtos.PaginatedTransactionsResponse list(UUID userId, LocalDate from, LocalDate to, UUID accountId, UUID categoryId, TransactionType transactionType, Direction direction, TransactionSource source, String merchant, BigDecimal minAmount, BigDecimal maxAmount, int page, int size) {
        userService.getEntity(userId); int clamped=Math.min(Math.max(size,1),200);
        Specification<Transaction> spec = spec(userId,from,to,accountId,categoryId,transactionType,direction,source,merchant,minAmount,maxAmount);
        Page<Transaction> p = repository.findAll(spec, PageRequest.of(Math.max(page,0), clamped, Sort.by(Sort.Order.desc("transactionDate"), Sort.Order.desc("createdAt"))));
        return new TransactionDtos.PaginatedTransactionsResponse(p.getContent().stream().map(this::toResponse).toList(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
    public TransactionDtos.TransactionResponse get(UUID id) { return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"))); }
    public TransactionDtos.TransactionResponse updateCategory(UUID transactionId, TransactionDtos.UpdateTransactionCategoryRequest request) { Transaction t=getEntity(transactionId); if (request.categoryId() == null) t.setCategoryId(null); else { categoryService.resolveCategoryForUser(t.getUserId(), request.categoryId()); t.setCategoryId(request.categoryId()); } return toResponse(repository.save(t)); }
    public TransactionDtos.TransactionResponse update(UUID transactionId, TransactionDtos.UpdateTransactionRequest r){ Transaction t=getEntity(transactionId); if(r.categoryId()!=null) categoryService.resolveCategoryForUser(t.getUserId(), r.categoryId()); applyUpdate(t,r.transactionDate(),r.amount(),r.direction(),r.transactionType(),r.source(),r.rawDescription(),r.merchant(),r.notes(),r.categoryId(),r.internalTransfer(),r.investmentTransfer(),r.debtPayment()); return toResponse(repository.save(t)); }
    public void delete(UUID transactionId){ repository.delete(getEntity(transactionId)); }
    @Transactional
    public TransactionDtos.BulkCategoryUpdateResponse bulkUpdateCategory(UUID userId, TransactionDtos.BulkCategoryUpdateRequest request){ userService.getEntity(userId); if(request.categoryId()!=null) categoryService.resolveCategoryForUser(userId, request.categoryId()); List<Transaction> tx=repository.findAllById(request.transactionIds()); if(tx.size()!=request.transactionIds().size()) throw new ResourceNotFoundException("Transaction not found"); if(tx.stream().anyMatch(t->!t.getUserId().equals(userId))) throw new IllegalArgumentException("Transaction does not belong to user"); tx.forEach(t->t.setCategoryId(request.categoryId())); repository.saveAll(tx); return new TransactionDtos.BulkCategoryUpdateResponse(request.transactionIds().size(), tx.size()); }
    private Specification<Transaction> spec(UUID userId, LocalDate from, LocalDate to, UUID accountId, UUID categoryId, TransactionType transactionType, Direction direction, TransactionSource source, String merchant, BigDecimal minAmount, BigDecimal maxAmount){ return (root, query, cb) -> {List<Predicate> predicates = new ArrayList<>(); predicates.add(cb.equal(root.get("userId"), userId)); if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), from)); if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), to)); if (accountId != null) predicates.add(cb.equal(root.get("accountId"), accountId)); if (categoryId != null) predicates.add(cb.equal(root.get("categoryId"), categoryId)); if (transactionType != null) predicates.add(cb.equal(root.get("transactionType"), transactionType)); if (direction != null) predicates.add(cb.equal(root.get("direction"), direction)); if (source != null) predicates.add(cb.equal(root.get("source"), source)); if (merchant != null && !merchant.isBlank()) predicates.add(cb.like(cb.lower(root.get("merchant")), "%" + merchant.toLowerCase() + "%")); if (minAmount != null) predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount)); if (maxAmount != null) predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount)); return cb.and(predicates.toArray(new Predicate[0]));}; }
    private Transaction getEntity(UUID id){ return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Transaction not found")); }
    private void applyUpdate(Transaction t, LocalDate date, BigDecimal amount, Direction direction, TransactionType type, TransactionSource source, String rawDescription, String merchant, String notes, UUID categoryId, Boolean internalTransfer, Boolean investmentTransfer, Boolean debtPayment){ t.setTransactionDate(date); t.setAmount(amount); t.setDirection(direction); t.setTransactionType(type); t.setSource(source); t.setRawDescription(rawDescription); t.setMerchant(merchant); t.setNotes(notes); t.setCategoryId(categoryId); t.setInternalTransfer(Boolean.TRUE.equals(internalTransfer)); t.setInvestmentTransfer(Boolean.TRUE.equals(investmentTransfer)); t.setDebtPayment(Boolean.TRUE.equals(debtPayment)); }
    private TransactionDtos.TransactionResponse toResponse(Transaction t){ return new TransactionDtos.TransactionResponse(t.getId(),t.getUserId(),t.getAccountId(),t.getCategoryId(),t.getTransactionDate(), t.getAmount(),t.getDirection(),t.getTransactionType(),t.getSource(),t.getRawDescription(),t.getMerchant(),t.getNotes(), t.isInternalTransfer(),t.isInvestmentTransfer(),t.isDebtPayment(),t.getImportHash(),t.getCreatedAt(),t.getUpdatedAt()); }
}
