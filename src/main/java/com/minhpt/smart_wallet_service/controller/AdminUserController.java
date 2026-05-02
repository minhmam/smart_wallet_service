package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<ApiResponse<UserResponse>> resetPassword(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.resetPassword(userId)));
    }

    @PutMapping("/{userId}/lock")
    public ResponseEntity<ApiResponse<UserResponse>> lock(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.lock(userId)));
    }

    @PutMapping("/{userId}/unlock")
    public ResponseEntity<ApiResponse<UserResponse>> unlock(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.unlock(userId)));
    }
}
