package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.dto.response.WalletPieChartItemResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletPieChartResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletSummaryResponse;
import com.minhpt.smart_wallet_service.model.AccountBalance;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
import com.minhpt.smart_wallet_service.repository.TransactionRepository;
import com.minhpt.smart_wallet_service.service.WalletSummaryService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletSummaryServiceImpl implements WalletSummaryService {

    private static final int TOP_CATEGORY_LIMIT = 5;
    private static final String OTHER_CATEGORY_NAME = "Khác";
    private static final String DEFAULT_OTHER_COLOR = "#9CA3AF";

    private final AccountBalanceRepository accountBalanceRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticationUtil authenticationUtil;

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
                            .categoryName(rawItem[1] != null ? rawItem[1].toString() : OTHER_CATEGORY_NAME)
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

    private void validatePieChartInput(Integer month, Integer year, String type) {
        if (month == null || year == null) {
            throw new IllegalArgumentException("Month and year are required");
        }

        YearMonth.of(year, month);

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type is required");
        }

        String normalizedType = type.trim().toUpperCase();
        if (!"INCOME".equals(normalizedType) && !"EXPENSE".equals(normalizedType)) {
            throw new IllegalArgumentException("Type must be either INCOME or EXPENSE");
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
                            .categoryName(OTHER_CATEGORY_NAME)
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
}
