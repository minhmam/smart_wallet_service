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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment-transaction")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentTransactionResponse>> create(@Valid @RequestBody CreatePaymentTransactionRequest req, HttpServletRequest request){

        return ResponseEntity.ok(
                ApiResponse.<PaymentTransactionResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message(Constant.SUCCESS)
                        .data(paymentTransactionService.create(req, IpUtil.getClientIp(request)))
                        .build()
        );
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<ApiResponse<String>> vnpayReturn(@RequestParam Map<String, String> params) {

        String message = paymentTransactionService.handleVnpayReturn(params);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(message)
                        .build()
        );
    }
}
