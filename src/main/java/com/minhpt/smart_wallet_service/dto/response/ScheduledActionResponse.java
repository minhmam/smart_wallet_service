package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledActionResponse {
    private Long id;
    private Long userId;
    private String actionType;
    private String scheduleState;
    private String recurrenceType;
    private Integer intervalValue;
    private LocalDateTime startAt;
    private LocalDateTime nextRunAt;
    private LocalDateTime lastRunAt;
    private LocalDateTime endAt;
    private String daysOfWeek;
    private Integer dayOfMonth;
    private String lastExecutionStatus;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private Long categoryId;
    private Long savingGoalId;
    private String failureReason;
    private Integer retryCount;
}
