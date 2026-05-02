package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTokenRegisterRequest {

    @NotBlank(message = "FCM token is required")
    private String fcmToken;

    private String platform;

    private String deviceId;
}
