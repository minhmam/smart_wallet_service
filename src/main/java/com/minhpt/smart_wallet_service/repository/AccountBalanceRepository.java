package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    Optional<AccountBalance> findByUserId(Long userId);

}
