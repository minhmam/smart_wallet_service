package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledAction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_action_seq")
    @SequenceGenerator(
            name = "scheduled_action_seq",
            sequenceName = "scheduled_action_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "schedule_state", nullable = false)
    private String scheduleState;

    @Column(name = "recurrence_type", nullable = false)
    private String recurrenceType;

    @Column(name = "interval_value", nullable = false)
    private Integer intervalValue;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "next_run_at", nullable = false)
    private LocalDateTime nextRunAt;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "days_of_week", length = 100)
    private String daysOfWeek;

    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    @Column(name = "last_execution_status")
    private String lastExecutionStatus;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "description")
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "saving_goal_id")
    private Long savingGoalId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "retry_count")
    private Integer retryCount;
}
