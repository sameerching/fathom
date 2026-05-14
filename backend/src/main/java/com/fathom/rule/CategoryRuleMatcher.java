package com.fathom.rule;

import com.fathom.transaction.Transaction;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoryRuleMatcher {
    public Optional<CategoryRule> findFirstMatch(List<CategoryRule> rules, Transaction t){
        return rules.stream().filter(r -> matches(r,t)).findFirst();
    }
    private boolean matches(CategoryRule r, Transaction t){
        if(!r.isActive() || !r.getUserId().equals(t.getUserId())) return false;
        if(r.getTransactionType()!=null && r.getTransactionType()!=t.getTransactionType()) return false;
        if(r.getDirection()!=null && r.getDirection()!=t.getDirection()) return false;
        String field = switch (r.getRuleField()) { case MERCHANT -> t.getMerchant(); case RAW_DESCRIPTION -> t.getRawDescription(); };
        String left = norm(field); String right = norm(r.getMatchValue());
        if(left==null || right==null) return false;
        return switch (r.getMatchOperator()) {
            case CONTAINS -> left.contains(right);
            case EQUALS -> left.equals(right);
            case STARTS_WITH -> left.startsWith(right);
            case ENDS_WITH -> left.endsWith(right);
        };
    }
    private String norm(String v){ return v==null?null:v.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+"," "); }
}
