package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.WalletPieChartResponse;
import com.minhpt.smart_wallet_service.dto.response.WalletSumaryResponse;

public interface WalletSumaryService {

    WalletSumaryResponse getWalletSummary();

    WalletPieChartResponse getPieChart(Integer month, Integer year, String type);
}
