package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCountResponse {
    private Long totalUsers;
    private BigDecimal totalRevenueInMonth;
    private Long totalNewTransactions;
    private Long totalPremiumMembers;
}
