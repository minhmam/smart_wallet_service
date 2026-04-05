package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;

import java.math.BigDecimal;

public interface AccountBalanceService {

    AccountBalanceDTO saveOrUpdate(AccountBalanceDTO accountBalanceDTO);

    AccountBalanceDTO addBalance(BigDecimal amount);

    AccountBalanceDTO subtractBalance(BigDecimal amount);
}
