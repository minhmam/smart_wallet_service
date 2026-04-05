package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.ScheduledAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledActionRepository extends JpaRepository<ScheduledAction, Long> {

    Optional<ScheduledAction> findByIdAndUserIdAndStatus(Long id, Long userId, int status);

    List<ScheduledAction> findTop50ByScheduleStateAndNextRunAtLessThanEqualAndStatusOrderByNextRunAtAsc(
            String scheduleState,
            LocalDateTime nextRunAt,
            int status
    );
}
