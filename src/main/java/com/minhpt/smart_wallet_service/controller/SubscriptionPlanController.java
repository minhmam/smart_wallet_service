package com.minhpt.smart_wallet_service.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanRequest;
import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription-plan")
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionPlanService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionPlanService.getDetail(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> create(
            @Valid @RequestBody SubscriptionPlanRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionPlanService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionPlanService.update(id, request)));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> search(
            @RequestBody SubscriptionPlanSearchRequest request
    ) {
        Page<SubscriptionPlanResponse> page = subscriptionPlanService.search(request);
        return ResponseEntity.ok(ApiResponse.successPage(page.getContent(), page.getTotalElements()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subscriptionPlanService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
