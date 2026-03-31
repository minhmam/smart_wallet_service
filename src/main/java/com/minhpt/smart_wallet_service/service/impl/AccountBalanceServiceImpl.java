package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;
import com.minhpt.smart_wallet_service.mapper.AccountBalanceMapper;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
import com.minhpt.smart_wallet_service.service.AccountBalanceService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountBalanceServiceImpl implements AccountBalanceService {

    private final AuthenticationUtil authenticationUtil;
    private final AccountBalanceMapper accountBalanceMapper;
    private final AccountBalanceRepository accountBalanceRepository;

    @Override
    public AccountBalanceDTO saveOrUpdate(AccountBalanceDTO accountBalanceDTO) {

        User user = authenticationUtil.getCurrentUser();

        if(user == null){
            throw new RuntimeException("Lỗi user chưa login");
        }

        return null;
    }
}
