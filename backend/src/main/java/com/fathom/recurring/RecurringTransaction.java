package com.fathom.recurring;

import com.fathom.common.BaseEntity;
import com.fathom.transaction.Direction;
import com.fathom.transaction.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction extends BaseEntity {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "account_id") private UUID accountId;
    @Column(name = "category_id") private UUID categoryId;
    @Column(nullable = false) private String name;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Direction direction;
    @Enumerated(EnumType.STRING) @Column(name = "transaction_type", nullable = false) private TransactionType transactionType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RecurrenceFrequency frequency;
    @Column(name = "day_of_month") private Integer dayOfMonth;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Column(nullable = false) private boolean active = true;
    @Column(columnDefinition = "TEXT") private String notes;
    @PrePersist void init(){ if(id==null) id=UUID.randomUUID(); }
    public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getAccountId(){return accountId;} public UUID getCategoryId(){return categoryId;} public String getName(){return name;} public BigDecimal getAmount(){return amount;} public Direction getDirection(){return direction;} public TransactionType getTransactionType(){return transactionType;} public RecurrenceFrequency getFrequency(){return frequency;} public Integer getDayOfMonth(){return dayOfMonth;} public LocalDate getStartDate(){return startDate;} public LocalDate getEndDate(){return endDate;} public boolean isActive(){return active;} public String getNotes(){return notes;}
    public void setUserId(UUID v){userId=v;} public void setAccountId(UUID v){accountId=v;} public void setCategoryId(UUID v){categoryId=v;} public void setName(String v){name=v;} public void setAmount(BigDecimal v){amount=v;} public void setDirection(Direction v){direction=v;} public void setTransactionType(TransactionType v){transactionType=v;} public void setFrequency(RecurrenceFrequency v){frequency=v;} public void setDayOfMonth(Integer v){dayOfMonth=v;} public void setStartDate(LocalDate v){startDate=v;} public void setEndDate(LocalDate v){endDate=v;} public void setActive(boolean v){active=v;} public void setNotes(String v){notes=v;}
}
