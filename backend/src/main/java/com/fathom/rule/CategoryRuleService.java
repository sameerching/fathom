package com.fathom.rule;

import com.fathom.category.Category;
import com.fathom.category.CategoryRepository;
import com.fathom.common.ResourceNotFoundException;
import com.fathom.transaction.Transaction;
import com.fathom.transaction.TransactionRepository;
import com.fathom.user.UserService;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryRuleService {
    private final CategoryRuleRepository repository; private final UserService userService; private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository; private final CategoryRuleMatcher matcher;
    public CategoryRuleService(CategoryRuleRepository repository, UserService userService, CategoryRepository categoryRepository, TransactionRepository transactionRepository, CategoryRuleMatcher matcher){this.repository=repository;this.userService=userService;this.categoryRepository=categoryRepository;this.transactionRepository=transactionRepository;this.matcher=matcher;}

    @Transactional public CategoryRuleDtos.RuleResponse create(UUID userId, CategoryRuleDtos.UpsertRuleRequest r){ userService.getEntity(userId); validateCategory(userId,r.categoryId()); CategoryRule e=new CategoryRule(); map(r,e); e.setUserId(userId); return toResponse(repository.save(e)); }
    public List<CategoryRuleDtos.RuleResponse> list(UUID userId){ userService.getEntity(userId); return repository.findByUserIdOrderByPriorityAscCreatedAtAsc(userId).stream().map(this::toResponse).toList(); }
    @Transactional public CategoryRuleDtos.RuleResponse update(UUID ruleId, CategoryRuleDtos.UpsertRuleRequest r){ CategoryRule e=getEntity(ruleId); validateCategory(e.getUserId(), r.categoryId()); map(r,e); return toResponse(repository.save(e)); }
    @Transactional public void deactivate(UUID ruleId){ CategoryRule e=getEntity(ruleId); e.setActive(false); repository.save(e);}    
    @Transactional public CategoryRuleDtos.ApplyRulesResponse apply(UUID userId, LocalDate from, LocalDate to, boolean onlyUncategorized){ userService.getEntity(userId); var rules=repository.findByUserIdAndActiveTrueOrderByPriorityAscCreatedAtAsc(userId); var txns=transactionRepository.findByUserId(userId);
        int matched=0,updated=0,skipped=0; for(Transaction t:txns){ if(from!=null&&t.getTransactionDate().isBefore(from)) continue; if(to!=null&&t.getTransactionDate().isAfter(to)) continue; var hit=matcher.findFirstMatch(rules,t); if(hit.isEmpty()) continue; matched++; if(onlyUncategorized && t.getCategoryId()!=null){skipped++; continue;} if(Objects.equals(t.getCategoryId(), hit.get().getCategoryId())){skipped++; continue;} t.setCategoryId(hit.get().getCategoryId()); updated++; }
        return new CategoryRuleDtos.ApplyRulesResponse(matched,updated,skipped); }
    public Optional<UUID> matchCategoryId(UUID userId, Transaction t){ var rules=repository.findByUserIdAndActiveTrueOrderByPriorityAscCreatedAtAsc(userId); return matcher.findFirstMatch(rules,t).map(CategoryRule::getCategoryId); }

    private CategoryRule getEntity(UUID id){ return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Category rule not found")); }
    private void validateCategory(UUID userId, UUID categoryId){ Category c=categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category not found")); if(!(c.isSystemDefault() || userId.equals(c.getUserId()))) throw new IllegalArgumentException("Category not valid for user"); }
    private void map(CategoryRuleDtos.UpsertRuleRequest r, CategoryRule e){ e.setName(r.name().trim()); e.setPriority(r.priority()==null?100:r.priority()); e.setRuleField(r.ruleField()); e.setMatchOperator(r.matchOperator()); e.setMatchValue(r.matchValue().trim()); e.setCategoryId(r.categoryId()); e.setTransactionType(r.transactionType()); e.setDirection(r.direction()); e.setActive(r.active()==null||r.active()); }
    private CategoryRuleDtos.RuleResponse toResponse(CategoryRule r){ return new CategoryRuleDtos.RuleResponse(r.getId(),r.getUserId(),r.getName(),r.getPriority(),r.getRuleField(),r.getMatchOperator(),r.getMatchValue(),r.getCategoryId(),r.getTransactionType(),r.getDirection(),r.isActive(),r.getCreatedAt(),r.getUpdatedAt()); }
}
