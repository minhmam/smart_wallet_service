package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.SavingGoals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingGoalsRepository extends JpaRepository<SavingGoals, Long>, SavingGoalsRepositoryCustom {
    List<SavingGoals> findAllByCreatedByAndStatus(String username, int status);

    Optional<SavingGoals> findByIdAndStatus(Long id, int status);
}
