package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardCountResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardPremiumRevenueResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardUserGrowthResponse;
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

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<DashboardCountResponse>> getDashboardCount() {
        return ResponseEntity.ok(ApiResponse.success(service.getDashboardCount()));
    }

    @GetMapping("/user-growth")
    public ResponseEntity<ApiResponse<DashboardUserGrowthResponse>> getUserGrowth(
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getUserGrowth(year)));
    }

    @GetMapping("/premium-revenue")
    public ResponseEntity<ApiResponse<DashboardPremiumRevenueResponse>> getPremiumRevenue(
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getPremiumRevenue(year)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<WalletSummaryResponse>> getWalletSummary() {
        return ResponseEntity.ok(ApiResponse.success(service.getWalletSummary()));
    }

    @GetMapping("/pie-chart")
    public ResponseEntity<ApiResponse<WalletPieChartResponse>> getPieChart(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getPieChart(month, year, type)));
    }
}
