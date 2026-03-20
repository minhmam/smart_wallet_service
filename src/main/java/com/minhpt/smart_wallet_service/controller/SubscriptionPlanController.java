package com.minhpt.smart_wallet_service.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription-plan")
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(subscriptionPlanService.getAll())
                        .build()
        );
    }
}
