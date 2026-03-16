package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.LoginRequest;
import com.minhpt.smart_wallet_service.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest req);

}
