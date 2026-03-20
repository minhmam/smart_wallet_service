package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByIdAndStatus(Long id, int status);

    Optional<PaymentTransaction> findByOrderCode(String orderCode);
}
