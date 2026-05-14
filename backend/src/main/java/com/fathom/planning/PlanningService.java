package com.fathom.planning;

import com.fathom.recurring.*;
import com.fathom.transaction.Transaction;
import com.fathom.transaction.TransactionRepository;
import com.fathom.transaction.TransactionType;
import com.fathom.user.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class PlanningService {
    private final UserService userService; private final RecurringTransactionRepository recurringRepository; private final TransactionRepository transactionRepository;
    public PlanningService(UserService userService, RecurringTransactionRepository recurringRepository, TransactionRepository transactionRepository) { this.userService = userService; this.recurringRepository = recurringRepository; this.transactionRepository = transactionRepository; }
    public PlanningDtos.MonthlyPlanningSummaryResponse monthlySummary(UUID userId, YearMonth month){ userService.getEntity(userId); YearMonth target = month == null ? YearMonth.now() : month; LocalDate from = target.atDay(1), to = target.atEndOfMonth();
        Totals planned = new Totals(); for (RecurringTransaction rt : recurringRepository.findByUserIdAndActiveTrue(userId)) if (applies(rt,target,from,to)) planned.add(rt.getTransactionType(), rt.getAmount());
        Totals actual = new Totals(); List<Transaction> txns = transactionRepository.findAll((root, query, cb) -> cb.and(cb.equal(root.get("userId"), userId), cb.greaterThanOrEqualTo(root.get("transactionDate"), from), cb.lessThanOrEqualTo(root.get("transactionDate"), to))); for (Transaction t: txns) actual.add(t.getTransactionType(), t.getAmount());
        return planned.toResponse(userId, target.toString(), actual);
    }
    private boolean applies(RecurringTransaction rt, YearMonth target, LocalDate from, LocalDate to){ if(rt.getStartDate().isAfter(to)) return false; if(rt.getEndDate()!=null && rt.getEndDate().isBefore(from)) return false; return switch(rt.getFrequency()){ case MONTHLY -> true; case QUARTERLY -> ChronoUnit.MONTHS.between(YearMonth.from(rt.getStartDate()), target) % 3 == 0; case YEARLY -> target.getMonthValue() == rt.getStartDate().getMonthValue(); }; }
    private static class Totals { BigDecimal i=BigDecimal.ZERO,e=BigDecimal.ZERO,inv=BigDecimal.ZERO,l=BigDecimal.ZERO; void add(TransactionType t, BigDecimal a){ if(t==null||a==null)return; switch(t){case INCOME -> i=i.add(a); case EXPENSE -> e=e.add(a); case INVESTMENT -> inv=inv.add(a); case LIABILITY_PAYMENT -> l=l.add(a); default -> {} } }
        BigDecimal net(){ return i.subtract(e).subtract(inv).subtract(l); } BigDecimal s(BigDecimal v){ return v.setScale(2, RoundingMode.HALF_UP);} PlanningDtos.MonthlyPlanningSummaryResponse toResponse(UUID userId, String month, Totals actual){ BigDecimal pNet=s(net()), aNet=s(actual.net()); return new PlanningDtos.MonthlyPlanningSummaryResponse(userId, month, s(i), s(actual.i), s(actual.i.subtract(i)), s(e), s(actual.e), s(actual.e.subtract(e)), s(inv), s(actual.inv), s(actual.inv.subtract(inv)), s(l), s(actual.l), s(actual.l.subtract(l)), pNet, aNet, s(aNet.subtract(pNet))); }}
}
