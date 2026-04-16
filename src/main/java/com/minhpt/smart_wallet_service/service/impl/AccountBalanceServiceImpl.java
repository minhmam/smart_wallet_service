package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;
import com.minhpt.smart_wallet_service.mapper.AccountBalanceMapper;
import com.minhpt.smart_wallet_service.model.AccountBalance;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
import com.minhpt.smart_wallet_service.service.AccountBalanceService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountBalanceServiceImpl implements AccountBalanceService {

    private final AuthenticationUtil authenticationUtil;
    private final AccountBalanceMapper accountBalanceMapper;
    private final AccountBalanceRepository accountBalanceRepository;

    @Override
    @Transactional
    public AccountBalanceDTO saveOrUpdate(AccountBalanceDTO accountBalanceDTO) {
        User loginUser = getCurrentUser();
        validateBalanceValue(accountBalanceDTO.getBalance());

        AccountBalance accountBalance = getOrCreateAccountBalance(loginUser);
        accountBalance.setUser(loginUser);
        accountBalance.setBalance(accountBalanceDTO.getBalance());

        AccountBalance savedAccountBalance = accountBalanceRepository.save(accountBalance);
        return accountBalanceMapper.toDTO(savedAccountBalance);
    }

    @Override
    @Transactional
    public AccountBalanceDTO addBalance(BigDecimal amount) {
        return adjustBalance(amount, true);
    }

    @Override
    @Transactional
    public AccountBalanceDTO subtractBalance(BigDecimal amount) {
        return adjustBalance(amount, false);
    }

    private AccountBalanceDTO adjustBalance(BigDecimal amount, boolean isAddition) {
        User loginUser = getCurrentUser();
        validatePositiveAmount(amount);

        AccountBalance accountBalance = getOrCreateAccountBalance(loginUser);
        BigDecimal currentBalance = getSafeBalance(accountBalance);
        BigDecimal updatedBalance = isAddition
                ? currentBalance.add(amount)
                : currentBalance.subtract(amount);

        if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }

        accountBalance.setUser(loginUser);
        accountBalance.setBalance(updatedBalance);

        AccountBalance savedAccountBalance = accountBalanceRepository.save(accountBalance);
        return accountBalanceMapper.toDTO(savedAccountBalance);
    }

    private AccountBalance getOrCreateAccountBalance(User loginUser) {
        return accountBalanceRepository.findByUserId(loginUser.getId())
                .orElseGet(() -> AccountBalance.builder()
                        .user(loginUser)
                        .balance(BigDecimal.ZERO)
                        .build());
    }

    private BigDecimal getSafeBalance(AccountBalance accountBalance) {
        return accountBalance.getBalance() == null ? BigDecimal.ZERO : accountBalance.getBalance();
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private void validateBalanceValue(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance must not be null");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must not be negative");
        }
    }

    private User getCurrentUser() {
        User loginUser = authenticationUtil.getCurrentUser();
        if (loginUser == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }
        return loginUser;
    }
}
