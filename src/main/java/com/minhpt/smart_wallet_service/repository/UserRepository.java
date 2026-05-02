package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByEmail(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByIdAndStatus(Long id, int status);

    @Override
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    List<User> findAll();

    @Query(value = """
            select count(*)
            from users u
            where u.status = :status
            """, nativeQuery = true)
    Long countUsersByStatus(@Param("status") int status);

    @Query(value = """
            select count(*)
            from users u
            where u.status = :status
              and u.premium_expired_at > :now
            """, nativeQuery = true)
    Long countPremiumUsers(
            @Param("status") int status,
            @Param("now") LocalDateTime now
    );

    @Query(value = """
            select cast(extract(month from u.created_at) as int) as month,
                   count(u.id) as total_users
            from users u
            where u.status = :status
              and u.created_at >= :startDate
              and u.created_at < :endDate
            group by cast(extract(month from u.created_at) as int)
            order by cast(extract(month from u.created_at) as int)
            """, nativeQuery = true)
    List<Object[]> countNewUsersByMonth(
            @Param("status") int status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
