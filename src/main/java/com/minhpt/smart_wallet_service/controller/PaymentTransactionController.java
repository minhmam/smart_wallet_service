package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.CreatePaymentTransactionRequest;
import com.minhpt.smart_wallet_service.dto.response.PaymentTransactionResponse;
import com.minhpt.smart_wallet_service.service.PaymentTransactionService;
import com.minhpt.smart_wallet_service.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment-transaction")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> create(
            @Valid @RequestBody CreatePaymentTransactionRequest req,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentTransactionService.create(req, IpUtil.getClientIp(request))));
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<ApiResponse<String>> vnpayReturn(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentTransactionService.handleVnpayReturn(request)));
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
        log.info("IPN call from VNPAY");
        return ResponseEntity.ok(paymentTransactionService.handleVnpayIpn(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentTransactionService.getDetails(id)));
    }
}
