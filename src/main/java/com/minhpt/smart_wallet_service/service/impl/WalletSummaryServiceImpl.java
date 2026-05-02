package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.DashboardCountResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardPremiumRevenueItemResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardPremiumRevenueResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardUserGrowthItemResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardUserGrowthResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletPieChartItemResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletPieChartResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletSummaryResponse;
import com.minhpt.smart_wallet_service.enums.TransactionType;
import com.minhpt.smart_wallet_service.i18n.MessageResolver;
import com.minhpt.smart_wallet_service.model.AccountBalance;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
import com.minhpt.smart_wallet_service.repository.PaymentTransactionRepository;
import com.minhpt.smart_wallet_service.repository.TransactionRepository;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.WalletSummaryService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletSummaryServiceImpl implements WalletSummaryService {

    private static final int TOP_CATEGORY_LIMIT = 5;
    private static final String OTHER_CATEGORY_KEY = "wallet.category.other";
    private static final String DEFAULT_OTHER_COLOR = "#9CA3AF";

    private final AccountBalanceRepository accountBalanceRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final AuthenticationUtil authenticationUtil;
    private final MessageResolver messageResolver;

    @Override
    public WalletSummaryResponse getWalletSummary() {
        User loginUser = authenticationUtil.getCurrentUser();

        BigDecimal totalIncome = transactionRepository.getTotalIncomeByUserId(loginUser.getUsername());
        BigDecimal totalExpense = transactionRepository.getTotalExpenseByUserId(loginUser.getUsername());
        BigDecimal balance = accountBalanceRepository.findByUserId(loginUser.getId())
                .orElse(new AccountBalance()).getBalance();

        return WalletSummaryResponse.builder()
                .balance(balance)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .build();
    }

    @Override
    public WalletPieChartResponse getPieChart(Integer month, Integer year, String type) {
        validatePieChartInput(month, year, type);

        User loginUser = authenticationUtil.getCurrentUser();
        List<Object[]> rawItems = transactionRepository.getPieChartDataByUserIdAndMonthAndYearAndType(
                loginUser.getId(),
                month,
                year,
                type.trim().toUpperCase()
        );

        List<WalletPieChartItemResponse> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Object[] rawItem : rawItems) {
            BigDecimal amount = rawItem[4] != null ? new BigDecimal(rawItem[4].toString()) : BigDecimal.ZERO;
            totalAmount = totalAmount.add(amount);

            items.add(
                    WalletPieChartItemResponse.builder()
                            .categoryId(rawItem[0] != null ? ((Number) rawItem[0]).longValue() : null)
                            .categoryName(rawItem[1] != null ? rawItem[1].toString() : resolveOtherCategoryName())
                            .icon(rawItem[2] != null ? rawItem[2].toString() : null)
                            .color(rawItem[3] != null ? rawItem[3].toString() : null)
                            .amount(amount)
                            .other(Boolean.FALSE)
                            .build()
            );
        }

        List<WalletPieChartItemResponse> normalizedItems = mergeOtherItems(items);
        applyPercentage(normalizedItems, totalAmount);

        return WalletPieChartResponse.builder()
                .month(month)
                .year(year)
                .type(type.trim().toUpperCase())
                .totalAmount(totalAmount)
                .items(normalizedItems)
                .build();
    }

    @Override
    public DashboardCountResponse getDashboardCount() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        return DashboardCountResponse.builder()
                .totalUsers(userRepository.countUsersByStatus(Constant.NOT_DELETE))
                .totalRevenueInMonth(defaultAmount(paymentTransactionRepository.getTotalSuccessfulRevenueBetween(
                        Constant.STATE_SUCCESS,
                        Constant.NOT_DELETE,
                        startOfMonth,
                        startOfNextMonth
                )))
                .totalNewTransactions(paymentTransactionRepository.countSuccessfulTransactions(
                        Constant.STATE_SUCCESS,
                        Constant.NOT_DELETE
                ))
                .totalPremiumMembers(userRepository.countPremiumUsers(Constant.NOT_DELETE, now))
                .build();
    }

    @Override
    public DashboardUserGrowthResponse getUserGrowth(Integer year) {
        int targetYear = resolveYear(year);
        LocalDateTime startOfYear = Year.of(targetYear).atDay(1).atStartOfDay();
        LocalDateTime startOfNextYear = Year.of(targetYear + 1).atDay(1).atStartOfDay();

        Map<Integer, Long> totalUsersByMonth = new HashMap<>();
        for (Object[] item : userRepository.countNewUsersByMonth(
                Constant.NOT_DELETE,
                startOfYear,
                startOfNextYear
        )) {
            totalUsersByMonth.put(toInteger(item[0]), toLong(item[1]));
        }

        List<DashboardUserGrowthItemResponse> items = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            items.add(DashboardUserGrowthItemResponse.builder()
                    .month(month)
                    .totalUsers(totalUsersByMonth.getOrDefault(month, 0L))
                    .build());
        }

        return DashboardUserGrowthResponse.builder()
                .year(targetYear)
                .items(items)
                .build();
    }

    @Override
    public DashboardPremiumRevenueResponse getPremiumRevenue(Integer year) {
        int targetYear = resolveYear(year);
        LocalDateTime startOfYear = Year.of(targetYear).atDay(1).atStartOfDay();
        LocalDateTime startOfNextYear = Year.of(targetYear + 1).atDay(1).atStartOfDay();

        Map<Integer, DashboardPremiumRevenueItemResponse> revenueByMonth = new HashMap<>();
        for (Object[] item : paymentTransactionRepository.getMonthlyPremiumRevenue(
                Constant.STATE_SUCCESS,
                Constant.NOT_DELETE,
                startOfYear,
                startOfNextYear
        )) {
            Integer month = toInteger(item[0]);
            revenueByMonth.put(month, DashboardPremiumRevenueItemResponse.builder()
                    .month(month)
                    .totalRevenue(toBigDecimal(item[1]))
                    .totalTransactions(toLong(item[2]))
                    .build());
        }

        List<DashboardPremiumRevenueItemResponse> items = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            items.add(revenueByMonth.getOrDefault(month, DashboardPremiumRevenueItemResponse.builder()
                    .month(month)
                    .totalRevenue(BigDecimal.ZERO)
                    .totalTransactions(0L)
                    .build()));
        }

        return DashboardPremiumRevenueResponse.builder()
                .year(targetYear)
                .items(items)
                .build();
    }

    private void validatePieChartInput(Integer month, Integer year, String type) {
        if (month == null || year == null) {
            throw new IllegalArgumentException("Month and year are required");
        }

        YearMonth.of(year, month);

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type is required");
        }

        if (!TransactionType.isValid(type)) {
            throw new IllegalArgumentException("Type must be either "
                    + TransactionType.INCOME.name() + " or " + TransactionType.EXPENSE.name());
        }
    }

    private List<WalletPieChartItemResponse> mergeOtherItems(List<WalletPieChartItemResponse> items) {
        if (items.size() <= TOP_CATEGORY_LIMIT) {
            return items;
        }

        List<WalletPieChartItemResponse> topItems = new ArrayList<>(items.subList(0, TOP_CATEGORY_LIMIT));
        BigDecimal otherAmount = BigDecimal.ZERO;

        for (int i = TOP_CATEGORY_LIMIT; i < items.size(); i++) {
            otherAmount = otherAmount.add(items.get(i).getAmount());
        }

        if (otherAmount.compareTo(BigDecimal.ZERO) > 0) {
            topItems.add(
                    WalletPieChartItemResponse.builder()
                            .categoryId(null)
                            .categoryName(resolveOtherCategoryName())
                            .icon(null)
                            .color(DEFAULT_OTHER_COLOR)
                            .amount(otherAmount)
                            .other(Boolean.TRUE)
                            .build()
            );
        }

        return topItems;
    }

    private void applyPercentage(List<WalletPieChartItemResponse> items, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            for (WalletPieChartItemResponse item : items) {
                item.setPercentage(BigDecimal.ZERO);
            }
            return;
        }

        for (WalletPieChartItemResponse item : items) {
            BigDecimal percentage = item.getAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalAmount, 2, RoundingMode.HALF_UP);
            item.setPercentage(percentage);
        }
    }

    private String resolveOtherCategoryName() {
        return messageResolver.get(OTHER_CATEGORY_KEY);
    }

    private int resolveYear(Integer year) {
        return year != null ? Year.of(year).getValue() : Year.now().getValue();
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private Integer toInteger(Object value) {
        return value != null ? ((Number) value).intValue() : null;
    }

    private Long toLong(Object value) {
        return value != null ? ((Number) value).longValue() : 0L;
    }

    private BigDecimal toBigDecimal(Object value) {
        return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
    }
}
