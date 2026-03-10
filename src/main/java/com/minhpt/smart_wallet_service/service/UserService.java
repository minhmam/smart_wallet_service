package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.UserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;

public interface UserService {

    void createUser(UserCreateRequest request);

    void updateUser(UserUpdateRequest request);

    UserResponse getById();
}
