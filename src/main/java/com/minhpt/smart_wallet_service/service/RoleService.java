package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.RoleResponse;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    UserResponse assignRoles(Long userId, Set<String> roleNames);
}
