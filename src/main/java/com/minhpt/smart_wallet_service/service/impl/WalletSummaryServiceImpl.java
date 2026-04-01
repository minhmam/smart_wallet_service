package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.dto.response.WalletSumaryResponse;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
import com.minhpt.smart_wallet_service.repository.TransactionRepository;
import com.minhpt.smart_wallet_service.service.WalletSumaryService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletSummaryServiceImpl implements WalletSumaryService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public WalletSumaryResponse getWalletSummary() {

        User loginUser = authenticationUtil.getCurrentUser();

        BigDecimal totalIncome = transactionRepository.getTotalIncomeByUserId(loginUser.getId());
        BigDecimal totalExpense = transactionRepository.getTotalExpenseByUserId(loginUser.getId());
        BigDecimal balance = accountBalanceRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh khoản ứng với userId = " + loginUser.getId())).getBalance();

        WalletSumaryResponse walletSumaryResponse = new WalletSumaryResponse(balance, totalIncome, totalExpense);

        return walletSumaryResponse;
    }
}
