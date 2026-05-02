package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.model.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long>, NotificationScheduleRepositoryCustom {

    Optional<NotificationSchedule> findByIdAndStatus(Long id, int status);

    List<NotificationSchedule> findTop50ByScheduleStateAndNextRunAtLessThanEqualAndStatusOrderByNextRunAtAsc(
            String scheduleState,
            LocalDateTime nextRunAt,
            int status
    );
}
