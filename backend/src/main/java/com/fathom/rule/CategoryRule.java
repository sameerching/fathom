package com.fathom.rule;

import com.fathom.common.BaseEntity;
import com.fathom.transaction.Direction;
import com.fathom.transaction.TransactionType;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "category_rules")
public class CategoryRule extends BaseEntity {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private int priority = 100;
    @Enumerated(EnumType.STRING) @Column(name = "rule_field", nullable = false) private RuleField ruleField;
    @Enumerated(EnumType.STRING) @Column(name = "match_operator", nullable = false) private MatchOperator matchOperator;
    @Column(name = "match_value", nullable = false) private String matchValue;
    @Column(name = "category_id", nullable = false) private UUID categoryId;
    @Enumerated(EnumType.STRING) @Column(name = "transaction_type") private TransactionType transactionType;
    @Enumerated(EnumType.STRING) @Column(name = "direction") private Direction direction;
    @Column(nullable = false) private boolean active = true;
    @PrePersist void init(){ if(id==null) id=UUID.randomUUID(); }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public String getName(){return name;} public int getPriority(){return priority;} public RuleField getRuleField(){return ruleField;} public MatchOperator getMatchOperator(){return matchOperator;} public String getMatchValue(){return matchValue;} public UUID getCategoryId(){return categoryId;} public TransactionType getTransactionType(){return transactionType;} public Direction getDirection(){return direction;} public boolean isActive(){return active;}
    public void setUserId(UUID v){userId=v;} public void setName(String v){name=v;} public void setPriority(int v){priority=v;} public void setRuleField(RuleField v){ruleField=v;} public void setMatchOperator(MatchOperator v){matchOperator=v;} public void setMatchValue(String v){matchValue=v;} public void setCategoryId(UUID v){categoryId=v;} public void setTransactionType(TransactionType v){transactionType=v;} public void setDirection(Direction v){direction=v;} public void setActive(boolean v){active=v;}
}
