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
public class WalletPieChartItemResponse {
    private Long categoryId;
    private String categoryName;
    private String icon;
    private String color;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Boolean other;
}
