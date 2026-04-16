package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;
import com.minhpt.smart_wallet_service.dto.request.AmountRequest;
import com.minhpt.smart_wallet_service.service.AccountBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account-balance")
@RequiredArgsConstructor
public class AccountBalanceController {

    private final AccountBalanceService accountBalanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountBalanceDTO>> create(@RequestBody AccountBalanceDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(accountBalanceService.saveOrUpdate(dto)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<AccountBalanceDTO>> deposit(@Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(ApiResponse.success(accountBalanceService.addBalance(request.getAmount())));
    }
}
