package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long>{
}
