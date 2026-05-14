package com.fathom.budget;

import com.fathom.category.Category;
import com.fathom.category.CategoryRepository;
import com.fathom.category.CategoryService;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.transaction.Transaction;
import com.fathom.transaction.TransactionRepository;
import com.fathom.transaction.TransactionType;
import com.fathom.user.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private final BudgetRepository repository; private final UserService userService; private final CategoryService categoryService; private final CategoryRepository categoryRepository; private final TransactionRepository transactionRepository;
    public BudgetService(BudgetRepository repository, UserService userService, CategoryService categoryService, CategoryRepository categoryRepository, TransactionRepository transactionRepository){this.repository=repository;this.userService=userService;this.categoryService=categoryService;this.categoryRepository=categoryRepository;this.transactionRepository=transactionRepository;}
    public BudgetDtos.BudgetResponse create(UUID userId, BudgetDtos.UpsertBudgetRequest r){ userService.getEntity(userId); validateCategory(userId,r.categoryId()); Budget b=new Budget(); apply(b,r); b.setUserId(userId); return toResponse(repository.save(b)); }
    public List<BudgetDtos.BudgetResponse> list(UUID userId, String month){ userService.getEntity(userId); List<Budget> items= month==null||month.isBlank()?repository.findByUserIdOrderByMonthDescNameAsc(userId):repository.findByUserIdAndMonthOrderByMonthDescNameAsc(userId,month); return items.stream().map(this::toResponse).toList();}
    public BudgetDtos.BudgetResponse update(UUID budgetId, BudgetDtos.UpsertBudgetRequest r){ Budget b=getEntity(budgetId); validateCategory(b.getUserId(), r.categoryId()); apply(b,r); return toResponse(repository.save(b)); }
    public BudgetDtos.BudgetResponse deactivate(UUID budgetId){ Budget b=getEntity(budgetId); b.setActive(false); return toResponse(repository.save(b)); }
    public List<BudgetDtos.BudgetSummaryItem> summary(UUID userId, String month){ userService.getEntity(userId); YearMonth ym=YearMonth.parse(month); LocalDate from=ym.atDay(1), to=ym.atEndOfMonth();
        List<Budget> budgets=repository.findByUserIdAndMonthAndActiveTrueOrderByNameAsc(userId,month);
        List<Transaction> tx=transactionRepository.findByUserId(userId).stream().filter(t->!t.getTransactionDate().isBefore(from)&&!t.getTransactionDate().isAfter(to)&&t.getTransactionType()== TransactionType.EXPENSE).toList();
        BigDecimal total=tx.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        Map<UUID,String> names=new HashMap<>(); categoryRepository.findAllById(budgets.stream().map(Budget::getCategoryId).filter(Objects::nonNull).toList()).forEach(c->names.put(c.getId(), c.getName()));
        return budgets.stream().map(b->{ BigDecimal actual=b.getCategoryId()==null?total:tx.stream().filter(t->Objects.equals(t.getCategoryId(),b.getCategoryId())).map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add); BigDecimal remaining=b.getAmount().subtract(actual); BigDecimal usage=actual.multiply(BigDecimal.valueOf(100)).divide(b.getAmount(),2, RoundingMode.HALF_UP); return new BudgetDtos.BudgetSummaryItem(b.getId(),b.getName(),b.getMonth(),b.getCategoryId(),names.get(b.getCategoryId()),b.getAmount(),actual,remaining,usage,actual.compareTo(b.getAmount())>0? BudgetDtos.BudgetStatus.OVER_BUDGET: BudgetDtos.BudgetStatus.UNDER_BUDGET); }).toList();
    }
    private void apply(Budget b, BudgetDtos.UpsertBudgetRequest r){ b.setName(r.name()); b.setMonth(r.month()); b.setCategoryId(r.categoryId()); b.setAmount(r.amount()); b.setActive(r.active()==null||r.active()); b.setNotes(r.notes()); }
    private void validateCategory(UUID userId, UUID categoryId){ if(categoryId!=null) categoryService.resolveCategoryForUser(userId, categoryId); }
    private Budget getEntity(UUID id){ return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Budget not found")); }
    private BudgetDtos.BudgetResponse toResponse(Budget b){ return new BudgetDtos.BudgetResponse(b.getId(),b.getUserId(),b.getCategoryId(),b.getName(),b.getMonth(),b.getAmount(),b.isActive(),b.getNotes(),b.getCreatedAt(),b.getUpdatedAt()); }
}
