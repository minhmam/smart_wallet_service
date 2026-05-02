package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByIdAndStatus(Long id, int status);

    Optional<PaymentTransaction> findByOrderCode(String orderCode);

    Optional<PaymentTransaction> findFirstByUserIdAndState(Long userId, String State);

    @Query(value = """
            select coalesce(sum(pt.amount), 0)
            from payment_transactions pt
            where pt.status = :status
              and upper(pt.state) = upper(:state)
              and pt.paid_at >= :startDate
              and pt.paid_at < :endDate
            """, nativeQuery = true)
    BigDecimal getTotalSuccessfulRevenueBetween(
            @Param("state") String state,
            @Param("status") int status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
            select count(*)
            from payment_transactions pt
            where pt.status = :status
              and upper(pt.state) = upper(:state)
            """, nativeQuery = true)
    Long countSuccessfulTransactions(
            @Param("state") String state,
            @Param("status") int status
    );

    @Query(value = """
            select cast(extract(month from pt.paid_at) as int) as month,
                   coalesce(sum(pt.amount), 0) as total_revenue,
                   count(pt.id) as total_transactions
            from payment_transactions pt
            where pt.status = :status
              and upper(pt.state) = upper(:state)
              and pt.paid_at >= :startDate
              and pt.paid_at < :endDate
            group by cast(extract(month from pt.paid_at) as int)
            order by cast(extract(month from pt.paid_at) as int)
            """, nativeQuery = true)
    List<Object[]> getMonthlyPremiumRevenue(
            @Param("state") String state,
            @Param("status") int status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
