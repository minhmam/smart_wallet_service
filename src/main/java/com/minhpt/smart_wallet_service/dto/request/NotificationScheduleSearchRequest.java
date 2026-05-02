package com.minhpt.smart_wallet_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationScheduleSearchRequest extends BaseRequest {
    private String targetGroup;
    private String scheduleState;
    private String recurrenceType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
