package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.NotificationTokenRegisterRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationTokenResponse;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.model.UserNotificationToken;
import com.minhpt.smart_wallet_service.repository.UserNotificationTokenRepository;
import com.minhpt.smart_wallet_service.service.NotificationTokenService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationTokenServiceImpl implements NotificationTokenService {

    private final UserNotificationTokenRepository userNotificationTokenRepository;
    private final AuthenticationUtil authenticationUtil;

    @Override
    @Transactional
    public NotificationTokenResponse register(NotificationTokenRegisterRequest request) {
        User loginUser = authenticationUtil.getCurrentUser();
        String fcmToken = normalizeToken(request.getFcmToken());

        UserNotificationToken token = userNotificationTokenRepository.findByFcmToken(fcmToken)
                .orElseGet(() -> UserNotificationToken.builder()
                        .fcmToken(fcmToken)
                        .build());

        token.setUser(loginUser);
        token.setPlatform(normalizeNullable(request.getPlatform()));
        token.setDeviceId(normalizeNullable(request.getDeviceId()));
        token.setActive(Boolean.TRUE);
        token.setLastUsedAt(LocalDateTime.now());
        token.setStatus(Constant.NOT_DELETE);

        return toResponse(userNotificationTokenRepository.save(token));
    }

    private String normalizeToken(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FCM token must not be blank");
        }

        return value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private NotificationTokenResponse toResponse(UserNotificationToken token) {
        return NotificationTokenResponse.builder()
                .id(token.getId())
                .userId(token.getUser() != null ? token.getUser().getId() : null)
                .platform(token.getPlatform())
                .deviceId(token.getDeviceId())
                .active(token.getActive())
                .lastUsedAt(token.getLastUsedAt())
                .build();
    }
}
