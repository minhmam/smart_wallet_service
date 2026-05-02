package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.UserResponse;

public interface AdminUserService {

    UserResponse resetPassword(Long userId);

    UserResponse lock(Long userId);

    UserResponse unlock(Long userId);
}
