package com.minhpt.smart_wallet_service.controller;


import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.UserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.model.User;
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

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest req) {
        UserResponse user = userService.createUser(req);

        ApiResponse<UserResponse> res = ApiResponse.<UserResponse>builder()
                .status(200)
                .message("User created successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req){
        UserResponse updateUser = userService.updateUser(req);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Update successfully")
                        .build()
        );
    }



}
