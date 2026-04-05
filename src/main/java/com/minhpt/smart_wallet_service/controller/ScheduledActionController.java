package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.ScheduledGoalDepositCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.ScheduledTransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.ScheduledActionResponse;
import com.minhpt.smart_wallet_service.service.ScheduledActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduled-actions")
@RequiredArgsConstructor
public class ScheduledActionController {

    private final ScheduledActionService scheduledActionService;

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse<ScheduledActionResponse>> createTransactionSchedule(
            @Valid @RequestBody ScheduledTransactionCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<ScheduledActionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(scheduledActionService.createTransactionSchedule(request))
                        .build()
        );
    }

    @PostMapping("/goal-deposit")
    public ResponseEntity<ApiResponse<ScheduledActionResponse>> createGoalDepositSchedule(
            @Valid @RequestBody ScheduledGoalDepositCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<ScheduledActionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(scheduledActionService.createGoalDepositSchedule(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduledActionResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<ScheduledActionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(scheduledActionService.getDetail(id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduledActionResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<ScheduledActionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(scheduledActionService.cancel(id))
                        .build()
        );
    }
}
