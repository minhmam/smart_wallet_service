package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {

    List<Transaction> findAllByUserId(Long userID);

    Optional<Transaction> findByIdAndStatus(Long id, int status);

    @Query(value = """
        select coalesce(sum(t.amount), 0)
        from transactions t
        where t.status = 1
          and t.user_id = :userId
          and t.type = 'INCOME'
        """, nativeQuery = true)
    BigDecimal getTotalIncomeByUserId(Long userId);

    @Query(value = """
        select coalesce(sum(t.amount), 0)
        from transactions t
        where t.status = 1
          and t.user_id = :userId
          and t.type = 'EXPENSE'
        """, nativeQuery = true)
    BigDecimal getTotalExpenseByUserId(Long userId);

    List<Transaction> findByTypeAndUserIdAndStatus(String type, Long userId, Integer status );
}
