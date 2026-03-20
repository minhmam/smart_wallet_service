package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreatePaymentTransactionRequest {

    @NotNull(message = "Bạn phải lựa chọn gói thanh toán")
    private Long subscriptionPlanId;

    @NotBlank(message = "Bạn phải chọn phương thức thanh toán (Momo hoặc Vnpay)")
    private String provider;

    private String returnUrl;
}
