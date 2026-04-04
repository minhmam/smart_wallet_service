package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletPieChartResponse {
    private Integer month;
    private Integer year;
    private String type;
    private BigDecimal totalAmount;
    private List<WalletPieChartItemResponse> items;
}
