package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {
    List<Transaction> findAllByUserId(Long userID);

    Optional<Transaction> findByIdAndStatus(Long id, int status);
}
