package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.UserNotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationTokenRepository extends JpaRepository<UserNotificationToken, Long> {

    Optional<UserNotificationToken> findByFcmToken(String fcmToken);

    @Query("""
            select t
            from UserNotificationToken t
            join fetch t.user u
            where t.status = :status
              and t.active = true
              and u.status = :status
              and (
                    :targetGroup = 'ALL'
                    or (:targetGroup = 'PREMIUM' and u.premiumExpiredAt > :now)
                    or (:targetGroup = 'FREE' and (u.premiumExpiredAt is null or u.premiumExpiredAt <= :now))
              )
            """)
    List<UserNotificationToken> findActiveTokensByTargetGroup(
            @Param("targetGroup") String targetGroup,
            @Param("status") int status,
            @Param("now") LocalDateTime now
    );
}
