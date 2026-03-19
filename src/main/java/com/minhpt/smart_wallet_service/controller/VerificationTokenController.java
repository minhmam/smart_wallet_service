package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.VerificationTokenResponse;
import com.minhpt.smart_wallet_service.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationTokenResponse>> verify(@RequestParam String token) {

        verificationTokenService.verifyEmail(token);

        return ResponseEntity.ok(
                ApiResponse.<VerificationTokenResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .build()
        );
    }
}
