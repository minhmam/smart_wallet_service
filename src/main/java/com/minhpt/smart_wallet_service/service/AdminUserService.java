package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.AdminUserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;

public interface AdminUserService {

    UserResponse getDetail(Long userId);

    UserResponse resetPassword(Long userId);

    UserResponse update(Long userId, AdminUserUpdateRequest request);

    UserResponse lock(Long userId);

    UserResponse unlock(Long userId);
}
