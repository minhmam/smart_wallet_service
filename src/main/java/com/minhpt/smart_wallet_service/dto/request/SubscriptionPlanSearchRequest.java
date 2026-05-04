package com.minhpt.smart_wallet_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanSearchRequest extends BaseRequest {
    private String code;
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
