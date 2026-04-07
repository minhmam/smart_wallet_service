package com.minhpt.smart_wallet_service.controller;


import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.UserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Update successfully")
                        .data(userService.updateUser(req))
                        .build()
        );
    }
}
