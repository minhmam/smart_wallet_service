package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.RoleResponse;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.UserMapper;
import com.minhpt.smart_wallet_service.model.Role;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.RoleRepository;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Role::getName))
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public UserResponse assignRoles(Long userId, Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("At least one role is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by id = " + userId));

        Set<String> normalizedRoleNames = roleNames.stream()
                .map(this::normalizeRoleName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Role> roles = roleRepository.findAllByNameIn(normalizedRoleNames);
        if (roles.size() != normalizedRoleNames.size()) {
            Set<String> foundNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            Set<String> missingNames = normalizedRoleNames.stream()
                    .filter(roleName -> !foundNames.contains(roleName))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            throw new IllegalArgumentException("Roles not found: " + String.join(", ", missingNames));
        }

        user.replaceRoles(new LinkedHashSet<>(roles));

        return userMapper.toResponse(userRepository.save(user));
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name must not be blank");
        }

        String normalized = roleName.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }
}
