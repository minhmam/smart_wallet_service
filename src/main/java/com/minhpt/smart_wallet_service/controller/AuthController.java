package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.LoginRequest;
import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.AuthResponse;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.service.AuthService;
import com.minhpt.smart_wallet_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest req) {
        UserResponse user = userService.createUser(req);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                .status(200)
                .message("User created successfully")
                .data(user)
                .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest req) {

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(authService.login(req))
                        .build()
        );
    }
}
