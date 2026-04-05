package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.WalletPieChartResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletSummaryResponse;
import com.minhpt.smart_wallet_service.service.WalletSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doashboard")
@RequiredArgsConstructor
public class WalletSummaryController {

    private final WalletSummaryService service;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<WalletSummaryResponse>> getWalletSummary() {
        return ResponseEntity.ok(
                ApiResponse.<WalletSummaryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(service.getWalletSummary())
                        .build()
        );
    }

    @GetMapping("/pie-chart")
    public ResponseEntity<ApiResponse<WalletPieChartResponse>> getPieChart(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(
                ApiResponse.<WalletPieChartResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(service.getPieChart(month, year, type))
                        .build()
        );
    }
}
