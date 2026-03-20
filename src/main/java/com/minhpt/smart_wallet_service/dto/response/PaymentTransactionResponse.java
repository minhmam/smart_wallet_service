package com.minhpt.smart_wallet_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentTransactionResponse {

    private Long id;
    private Long userId;
    private Long subscriptionPlanId;
    private String provider;
    private String orderCode;
    private String providerREf;
    private Long amount;
    private String state;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
