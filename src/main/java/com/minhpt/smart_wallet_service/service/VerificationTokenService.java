package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.model.User;

public interface VerificationTokenService {

    void sendVerifyEmail(User user);

    void verifyEmail(String token);
}
