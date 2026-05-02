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
public class NotificationTokenResponse {
    private Long id;
    private Long userId;
    private String platform;
    private String deviceId;
    private Boolean active;
    private LocalDateTime lastUsedAt;
}
