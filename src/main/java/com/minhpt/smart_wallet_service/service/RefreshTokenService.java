package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.model.RefreshToken;

public interface RefreshTokenService {

    RefreshToken create(Long userId);

    RefreshToken verify(String token);
}
