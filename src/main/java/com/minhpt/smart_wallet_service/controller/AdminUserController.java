package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.AdminUserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getDetail(userId)));
    }

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<ApiResponse<UserResponse>> resetPassword(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.resetPassword(userId)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.update(userId, request)));
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
