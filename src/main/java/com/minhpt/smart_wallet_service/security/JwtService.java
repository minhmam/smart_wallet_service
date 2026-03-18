package com.minhpt.smart_wallet_service.security;

import com.minhpt.smart_wallet_service.model.User;

public interface JwtService {

    String generateToken(User user);

    String extractUserId(String token);

    boolean isValid(String token);
}
