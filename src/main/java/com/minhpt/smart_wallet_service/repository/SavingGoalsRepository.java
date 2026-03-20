package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingGoalsRepository extends JpaRepository<SavingGoal, Long>, SavingGoalsRepositoryCustom {
    List<SavingGoal> findAllByCreatedByAndStatus(String username, int status);

    Optional<SavingGoal> findByIdAndStatus(Long id, int status);
}
