package com.minhpt.smart_wallet_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionPlanResponse {
    private Long id;
    private String code;
    private String name;
    private Long price;
    private Long durationDays;
    private Integer status;
}
