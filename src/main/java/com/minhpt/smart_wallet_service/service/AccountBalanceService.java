package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;
import com.minhpt.smart_wallet_service.model.AccountBalance;

public interface AccountBalanceService {

    AccountBalanceDTO saveOrUpdate(AccountBalanceDTO accountBalanceDTO);

}
