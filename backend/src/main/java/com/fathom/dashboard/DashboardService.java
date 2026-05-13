package com.fathom.dashboard;

import com.fathom.category.Category;
import com.fathom.category.CategoryRepository;
import com.fathom.investment.InvestmentHoldingRepository;
import com.fathom.liability.LiabilityRepository;
import com.fathom.transaction.Transaction;
import com.fathom.transaction.TransactionRepository;
import com.fathom.transaction.TransactionType;
import com.fathom.user.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final InvestmentHoldingRepository investmentHoldingRepository;
    private final LiabilityRepository liabilityRepository;

    public DashboardService(UserService userService, TransactionRepository transactionRepository, CategoryRepository categoryRepository,
                            InvestmentHoldingRepository investmentHoldingRepository, LiabilityRepository liabilityRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.investmentHoldingRepository = investmentHoldingRepository;
        this.liabilityRepository = liabilityRepository;
    }

    public DashboardDtos.MonthlySummaryResponse monthlySummary(UUID userId, YearMonth month) {
        userService.getEntity(userId);
        YearMonth target = month == null ? YearMonth.now() : month;
        LocalDate from = target.atDay(1);
        LocalDate to = target.atEndOfMonth();
        List<Transaction> txns = transactionRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("userId"), userId),
                cb.greaterThanOrEqualTo(root.get("transactionDate"), from),
                cb.lessThanOrEqualTo(root.get("transactionDate"), to)
        ));
        Map<TransactionType, BigDecimal> totals = new EnumMap<>(TransactionType.class);
        for (Transaction t : txns) totals.merge(t.getTransactionType(), nz(t.getAmount()), BigDecimal::add);
        BigDecimal income = scale(totals.get(TransactionType.INCOME));
        BigDecimal expenses = scale(totals.get(TransactionType.EXPENSE));
        BigDecimal investments = scale(totals.get(TransactionType.INVESTMENT));
        BigDecimal liabilityPayments = scale(totals.get(TransactionType.LIABILITY_PAYMENT));
        BigDecimal transfers = scale(totals.get(TransactionType.TRANSFER));
        BigDecimal refunds = scale(totals.get(TransactionType.REFUND));
        BigDecimal adjustments = scale(totals.get(TransactionType.ADJUSTMENT));
        BigDecimal netCashFlow = scale(income.add(refunds).subtract(expenses).subtract(investments).subtract(liabilityPayments));
        BigDecimal savingsRate = income.compareTo(BigDecimal.ZERO) > 0
                ? scale(income.subtract(expenses).subtract(liabilityPayments).divide(income, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)))
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return new DashboardDtos.MonthlySummaryResponse(userId, target.toString(), income, expenses, investments, liabilityPayments,
                transfers, refunds, adjustments, netCashFlow, savingsRate);
    }

    public List<DashboardDtos.CategoryBreakdownItem> categoryBreakdown(UUID userId, LocalDate from, LocalDate to, TransactionType type) {
        userService.getEntity(userId);
        TransactionType targetType = type == null ? TransactionType.EXPENSE : type;
        List<Transaction> txns = transactionRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("userId"), userId), cb.equal(root.get("transactionType"), targetType),
                cb.greaterThanOrEqualTo(root.get("transactionDate"), from), cb.lessThanOrEqualTo(root.get("transactionDate"), to)
        ));
        Map<UUID, Category> categories = new HashMap<>();
        categoryRepository.findAllById(txns.stream().map(Transaction::getCategoryId).filter(Objects::nonNull).toList())
                .forEach(c -> categories.put(c.getId(), c));
        Map<UUID, BigDecimal> amountByCategory = new HashMap<>();
        Map<UUID, Long> countByCategory = new HashMap<>();
        for (Transaction t : txns) {
            UUID key = t.getCategoryId();
            amountByCategory.merge(key, nz(t.getAmount()), BigDecimal::add);
            countByCategory.merge(key, 1L, Long::sum);
        }
        return amountByCategory.entrySet().stream()
                .map(e -> {
                    Category category = categories.get(e.getKey());
                    return new DashboardDtos.CategoryBreakdownItem(
                            e.getKey(),
                            category == null ? "Uncategorized" : category.getName(),
                            targetType,
                            scale(e.getValue()),
                            countByCategory.getOrDefault(e.getKey(), 0L)
                    );
                })
                .sorted(Comparator.comparing(DashboardDtos.CategoryBreakdownItem::amount).reversed())
                .toList();
    }

    public DashboardDtos.NetWorthSummaryResponse netWorth(UUID userId) {
        userService.getEntity(userId);
        BigDecimal totalAssets = scale(investmentHoldingRepository.findByUserIdAndActiveTrue(userId).stream().map(h -> nz(h.getCurrentValue())).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal totalLiabilities = scale(liabilityRepository.findByUserIdAndActiveTrue(userId).stream().map(l -> nz(l.getOutstandingAmount())).reduce(BigDecimal.ZERO, BigDecimal::add));
        return new DashboardDtos.NetWorthSummaryResponse(userId, totalAssets, totalLiabilities, scale(totalAssets.subtract(totalLiabilities)));
    }

    private BigDecimal nz(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private BigDecimal scale(BigDecimal value) { return nz(value).setScale(2, RoundingMode.HALF_UP); }
}
