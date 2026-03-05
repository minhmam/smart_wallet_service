package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
}
