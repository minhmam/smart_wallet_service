package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.WalletSumaryResponse;
import com.minhpt.smart_wallet_service.service.WalletSumaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet-summary")
@RequiredArgsConstructor
public class WalletSummaryController {

    private final WalletSumaryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletSumaryResponse>> getWalletSummary(){
        return ResponseEntity.ok(
                ApiResponse.<WalletSumaryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(service.getWalletSummary())
                        .build()
        );
    }
}
