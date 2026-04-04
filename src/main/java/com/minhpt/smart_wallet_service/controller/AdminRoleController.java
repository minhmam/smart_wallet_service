package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.AssignRolesRequest;
import com.minhpt.smart_wallet_service.dto.response.RoleResponse;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(
                ApiResponse.<List<RoleResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(roleService.getAllRoles())
                        .build()
        );
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRolesRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(roleService.assignRoles(userId, request.getRoleNames()))
                        .build()
        );
    }
}
