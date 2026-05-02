package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationScheduleResponse {
    private Long id;
    private String title;
    private String content;
    private String targetGroup;
    private String scheduleState;
    private String recurrenceType;
    private LocalDateTime startAt;
    private LocalDateTime nextRunAt;
    private LocalDateTime lastRunAt;
    private String lastExecutionStatus;
    private Long lastTargetUserCount;
    private Long lastSuccessCount;
    private Long lastFailureCount;
    private String failureReason;
    private Integer retryCount;
    private LocalDateTime createdAt;
}
