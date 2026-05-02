package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleSearchRequest;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationScheduleResponse;
import com.minhpt.smart_wallet_service.service.NotificationScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class NotificationScheduleController {

    private final NotificationScheduleService notificationScheduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> create(
            @Valid @RequestBody NotificationScheduleCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(notificationScheduleService.create(request)));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<NotificationScheduleResponse>>> search(
            @RequestBody NotificationScheduleSearchRequest request
    ) {
        Page<NotificationScheduleResponse> page = notificationScheduleService.search(request);
        return ResponseEntity.ok(ApiResponse.successPage(page.getContent(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notificationScheduleService.getDetail(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationScheduleUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(notificationScheduleService.update(id, request)));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> disable(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notificationScheduleService.disable(id)));
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> enable(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notificationScheduleService.enable(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notificationScheduleService.delete(id)));
    }
}
