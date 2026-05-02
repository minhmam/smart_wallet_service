package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_schedule_seq")
    @SequenceGenerator(
            name = "notification_schedule_seq",
            sequenceName = "notification_schedule_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "target_group", nullable = false)
    private String targetGroup;

    @Column(name = "schedule_state", nullable = false)
    private String scheduleState;

    @Column(name = "recurrence_type", nullable = false)
    private String recurrenceType;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "next_run_at")
    private LocalDateTime nextRunAt;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "last_execution_status")
    private String lastExecutionStatus;

    @Column(name = "last_target_user_count")
    private Long lastTargetUserCount;

    @Column(name = "last_success_count")
    private Long lastSuccessCount;

    @Column(name = "last_failure_count")
    private Long lastFailureCount;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "retry_count")
    private Integer retryCount;
}
