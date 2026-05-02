package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.UserSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserRepositoryCustom {
    Page<UserResponse> search(UserSearchRequest req);
}
