package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.CreatePaymentTransactionRequest;
import com.minhpt.smart_wallet_service.dto.response.PaymentTransactionResponse;
import com.minhpt.smart_wallet_service.service.PaymentTransactionService;
import com.minhpt.smart_wallet_service.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment-transaction")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> create(@Valid @RequestBody CreatePaymentTransactionRequest req, HttpServletRequest request) {

        return ResponseEntity.ok(
                ApiResponse.<PaymentTransactionResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message(Constant.SUCCESS)
                        .data(paymentTransactionService.create(req, IpUtil.getClientIp(request)))
                        .build()
        );
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<ApiResponse<String>> vnpayReturn(HttpServletRequest request) {

        String message = paymentTransactionService.handleVnpayReturn(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(message)
                        .build()
        );
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
        log.info("IPN call from VNPAY");
        return ResponseEntity.ok(paymentTransactionService.handleVnpayIpn(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<PaymentTransactionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(paymentTransactionService.getDetails(id))
                        .build()
        );
    }
}
