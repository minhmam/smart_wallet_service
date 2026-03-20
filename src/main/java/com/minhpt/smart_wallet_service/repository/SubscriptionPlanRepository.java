package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findAllByStatus(int status);

    Optional<SubscriptionPlan> findByIdAndStatus(Long id, int status);
}
