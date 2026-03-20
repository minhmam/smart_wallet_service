package com.minhpt.smart_wallet_service.payment.vnpay.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class VnpayConfig {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;
}