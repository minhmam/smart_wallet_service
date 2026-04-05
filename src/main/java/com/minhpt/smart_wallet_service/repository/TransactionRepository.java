package com.minhpt.smart_wallet_service.repository;

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
              and t.created_by = :username
              and t.type = 'INCOME'
            """, nativeQuery = true)
    BigDecimal getTotalIncomeByUserId(String username);

    @Query(value = """
            select coalesce(sum(t.amount), 0)
            from transactions t
            where t.status = 1
              and t.created_by = :username
              and t.type = 'EXPENSE'
            """, nativeQuery = true)
    BigDecimal getTotalExpenseByUserId(String username);

    List<Transaction> findByTypeAndUserIdAndStatus(String type, Long userId, Integer status);

    @Query(value = """
            select
                c.id as categoryId,
                c.name as categoryName,
                c.icon as icon,
                c.color as color,
                coalesce(sum(t.amount), 0) as totalAmount
            from transactions t
            left join categories c on c.id = t.category_id
            where t.status = 1
              and t.user_id = :userId
              and t.type = :type
              and extract(month from t.transaction_date) = :month
              and extract(year from t.transaction_date) = :year
            group by c.id, c.name, c.icon, c.color
            order by totalAmount desc, c.id asc
            """, nativeQuery = true)
    List<Object[]> getPieChartDataByUserIdAndMonthAndYearAndType(
            @Param("userId") Long userId,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("type") String type
    );
}
