package com.minhpt.smart_wallet_service.payment.vnpay.service;

import com.minhpt.smart_wallet_service.model.PaymentTransaction;

import java.util.Map;

public interface VnpayService {

    String createPaymentUrl(PaymentTransaction transaction, String ipAddress);

    boolean verifyReturn(Map<String, String> params);

    boolean verifyIpn(Map<String, String> params);
}
