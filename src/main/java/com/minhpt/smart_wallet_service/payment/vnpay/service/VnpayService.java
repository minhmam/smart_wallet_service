package com.minhpt.smart_wallet_service.payment.vnpay.service;

import com.minhpt.smart_wallet_service.model.PaymentTransaction;

import java.util.Map;

public interface VnpayService {

    String createPaymentUrl(PaymentTransaction transaction, String ipAddress);

    String handleReturn(Map<String, String> params);

    String handleIpn(Map<String, String> params);

    boolean verifySignature(Map<String, String> params);
}
