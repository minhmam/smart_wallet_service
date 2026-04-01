package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.CreatePaymentTransactionRequest;
import com.minhpt.smart_wallet_service.dto.response.PaymentTransactionResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentTransactionService {
    PaymentTransactionResponse create(CreatePaymentTransactionRequest request, String ipAddress);

    String handleVnpayReturn(HttpServletRequest request);

    String handleVnpayIpn(HttpServletRequest request);

    PaymentTransactionResponse getDetails(Long id);

}
