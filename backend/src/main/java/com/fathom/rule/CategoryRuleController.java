package com.fathom.rule;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryRuleController {
    private final CategoryRuleService service;
    public CategoryRuleController(CategoryRuleService service){this.service=service;}
    @PostMapping("/api/users/{userId}/category-rules")
    public CategoryRuleDtos.RuleResponse create(@PathVariable UUID userId, @Valid @RequestBody CategoryRuleDtos.UpsertRuleRequest request){ return service.create(userId, request); }
    @GetMapping("/api/users/{userId}/category-rules")
    public List<CategoryRuleDtos.RuleResponse> list(@PathVariable UUID userId){ return service.list(userId); }
    @PatchMapping("/api/category-rules/{ruleId}")
    public CategoryRuleDtos.RuleResponse update(@PathVariable UUID ruleId, @Valid @RequestBody CategoryRuleDtos.UpsertRuleRequest request){ return service.update(ruleId, request); }
    @PatchMapping("/api/category-rules/{ruleId}/deactivate")
    public void deactivate(@PathVariable UUID ruleId){ service.deactivate(ruleId); }
    @PostMapping("/api/users/{userId}/category-rules/apply")
    public CategoryRuleDtos.ApplyRulesResponse apply(@PathVariable UUID userId, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to, @RequestParam(defaultValue = "true") boolean onlyUncategorized){ return service.apply(userId, from, to, onlyUncategorized); }
}
