package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.NotificationTokenRegisterRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationTokenResponse;
import com.minhpt.smart_wallet_service.service.NotificationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/tokens")
@RequiredArgsConstructor
public class NotificationTokenController {

    private final NotificationTokenService notificationTokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationTokenResponse>> register(
            @Valid @RequestBody NotificationTokenRegisterRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(notificationTokenService.register(request)));
    }
}
