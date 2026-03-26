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

    @Value("${vnpay.ipnUrl}")
    private String ipnUrl;

    @Value("${vnpay.version}")
    private String version;

    @Value("${vnpay.command}")
    private String command;

    @Value("${vnpay.currCode}")
    private String currCode;

    @Value("${vnpay.locale}")
    private String locale;

    @Value("${vnpay.orderType}")
    private String orderType;

    @Value("${vnpay.orderInfo}")
    private String orderInfo;

    @Value("${vnpay.successCode}")
    private String successCode;

    @Value("${vnpay.ipnResponseCodeHandled}")
    private String ipnResponseCodeHandled;

    @Value("${vnpay.ipnResponseCodeInvalidRequest}")
    private String ipnResponseCodeInvalidRequest;

    @Value("${vnpay.ipnResponseCodeInvalidChecksum}")
    private String ipnResponseCodeInvalidChecksum;

    @Value("${vnpay.ipnMessageConfirmSuccess}")
    private String ipnMessageConfirmSuccess;

    @Value("${vnpay.ipnMessageConfirmFailed}")
    private String ipnMessageConfirmFailed;

    @Value("${vnpay.ipnMessageInvalidRequest}")
    private String ipnMessageInvalidRequest;

    @Value("${vnpay.ipnMessageInvalidChecksum}")
    private String ipnMessageInvalidChecksum;

    @Value("${vnpay.returnMessageSuccess}")
    private String returnMessageSuccess;

    @Value("${vnpay.returnMessageFailed}")
    private String returnMessageFailed;

    @Value("${vnpay.returnMessageInvalidRequest}")
    private String returnMessageInvalidRequest;

    @Value("${vnpay.returnMessageInvalidChecksum}")
    private String returnMessageInvalidChecksum;
}