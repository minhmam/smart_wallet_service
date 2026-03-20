package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByIdAndState(Long id, String state);
}
