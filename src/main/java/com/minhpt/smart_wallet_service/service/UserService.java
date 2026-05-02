package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.UserSearchRequest;
import com.minhpt.smart_wallet_service.dto.request.UserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse update(UserUpdateRequest request);

    UserResponse getCurrentUser();

    Page<UserResponse> search(UserSearchRequest request);
}
