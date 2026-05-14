package com.fathom.budget;

import com.fathom.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class Budget extends BaseEntity {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "category_id")
    private UUID categoryId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 7)
    private String month;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(nullable = false)
    private boolean active = true;
    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist void init(){ if(id==null) id=UUID.randomUUID(); }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getCategoryId(){return categoryId;}
    public String getName(){return name;} public String getMonth(){return month;} public BigDecimal getAmount(){return amount;}
    public boolean isActive(){return active;} public String getNotes(){return notes;}
    public void setUserId(UUID v){userId=v;} public void setCategoryId(UUID v){categoryId=v;} public void setName(String v){name=v;}
    public void setMonth(String v){month=v;} public void setAmount(BigDecimal v){amount=v;} public void setActive(boolean v){active=v;} public void setNotes(String v){notes=v;}
}
