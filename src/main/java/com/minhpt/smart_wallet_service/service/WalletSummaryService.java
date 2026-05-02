package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.DashboardCountResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardPremiumRevenueResponse;
import com.minhpt.smart_wallet_service.dto.response.DashboardUserGrowthResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletPieChartResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletSummaryResponse;

public interface WalletSummaryService {

    WalletSummaryResponse getWalletSummary();

    WalletPieChartResponse getPieChart(Integer month, Integer year, String type);

    DashboardCountResponse getDashboardCount();

    DashboardUserGrowthResponse getUserGrowth(Integer year);

    DashboardPremiumRevenueResponse getPremiumRevenue(Integer year);
}
