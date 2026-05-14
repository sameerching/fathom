package com.fathom.recurring;

import com.fathom.account.FinancialAccountService;
import com.fathom.category.CategoryService;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.user.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RecurringTransactionService {
    private final RecurringTransactionRepository repository; private final UserService userService; private final FinancialAccountService accountService; private final CategoryService categoryService;
    public RecurringTransactionService(RecurringTransactionRepository repository, UserService userService, FinancialAccountService accountService, CategoryService categoryService) { this.repository = repository; this.userService = userService; this.accountService = accountService; this.categoryService = categoryService; }
    public RecurringTransactionDtos.RecurringTransactionResponse create(UUID userId, RecurringTransactionDtos.UpsertRecurringTransactionRequest request){ userService.getEntity(userId); validate(userId, request); RecurringTransaction rt = new RecurringTransaction(); apply(rt, userId, request); return toResponse(repository.save(rt)); }
    public List<RecurringTransactionDtos.RecurringTransactionResponse> list(UUID userId){ userService.getEntity(userId); return repository.findByUserId(userId, Sort.by(Sort.Order.desc("active"), Sort.Order.asc("name"))).stream().map(this::toResponse).toList(); }
    public RecurringTransactionDtos.RecurringTransactionResponse update(UUID id, RecurringTransactionDtos.UpsertRecurringTransactionRequest request){ RecurringTransaction rt = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found")); validate(rt.getUserId(), request); apply(rt, rt.getUserId(), request); return toResponse(repository.save(rt)); }
    public RecurringTransactionDtos.RecurringTransactionResponse deactivate(UUID id){ RecurringTransaction rt = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurring transaction not found")); rt.setActive(false); return toResponse(repository.save(rt)); }
    private void validate(UUID userId, RecurringTransactionDtos.UpsertRecurringTransactionRequest request){ if(request.accountId()!=null && !accountService.getEntity(request.accountId()).getUserId().equals(userId)) throw new IllegalArgumentException("Account not found"); if(request.categoryId()!=null) categoryService.resolveCategoryForUser(userId, request.categoryId()); }
    private void apply(RecurringTransaction rt, UUID userId, RecurringTransactionDtos.UpsertRecurringTransactionRequest r){ rt.setUserId(userId); rt.setAccountId(r.accountId()); rt.setCategoryId(r.categoryId()); rt.setName(r.name()); rt.setAmount(r.amount()); rt.setDirection(r.direction()); rt.setTransactionType(r.transactionType()); rt.setFrequency(r.frequency()); rt.setDayOfMonth(r.dayOfMonth()); rt.setStartDate(r.startDate()); rt.setEndDate(r.endDate()); rt.setActive(r.active()==null || r.active()); rt.setNotes(r.notes()); }
    private RecurringTransactionDtos.RecurringTransactionResponse toResponse(RecurringTransaction t){ return new RecurringTransactionDtos.RecurringTransactionResponse(t.getId(), t.getUserId(), t.getAccountId(), t.getCategoryId(), t.getName(), t.getAmount(), t.getDirection(), t.getTransactionType(), t.getFrequency(), t.getDayOfMonth(), t.getStartDate(), t.getEndDate(), t.isActive(), t.getNotes(), t.getCreatedAt(), t.getUpdatedAt()); }
}
