package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;
import com.minhpt.smart_wallet_service.mapper.AccountBalanceMapper;
import com.minhpt.smart_wallet_service.model.AccountBalance;
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

        User loginUser = authenticationUtil.getCurrentUser();

        if(loginUser == null){
            throw new RuntimeException("Lỗi user chưa login");
        }

        AccountBalance accountBalance = accountBalanceRepository.findByUserId(loginUser.getId())
                .orElse(new AccountBalance());

        accountBalance.setUser(loginUser);
        accountBalance.setBalance(accountBalanceDTO.getBalance());
        accountBalance.setCreatedBy(loginUser.getUsername());
        accountBalance.setUpdatedBy(loginUser.getUsername());

        AccountBalance saveAccountBalance = accountBalanceRepository.save(accountBalance);

        return accountBalanceMapper.toDTO(saveAccountBalance);
    }
}
