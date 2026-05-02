package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.NotificationTokenRegisterRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationTokenResponse;

public interface NotificationTokenService {
    NotificationTokenResponse register(NotificationTokenRegisterRequest request);
}
