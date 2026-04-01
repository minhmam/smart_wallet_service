package com.minhpt.smart_wallet_service.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.service.SavingGoalsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saving-goals")
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class SavingGoalsController {

    private final SavingGoalsService savingGoalsService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<SavingGoalsResponse>>> search(@RequestBody SavingGoalsSearchRequest req){
        Page<SavingGoalsResponse> page = savingGoalsService.search(req);
        return ResponseEntity.ok(
                ApiResponse.<List<SavingGoalsResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(page.getContent())
                        .total(page.getTotalElements())
                        .build()
        );
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<SavingGoalsResponse>> create(@Valid @RequestBody SavingGoalsCreateRequest req){
        return ResponseEntity.ok(
                ApiResponse.<SavingGoalsResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(savingGoalsService.create(req))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SavingGoalsResponse>> update(@PathVariable Long id, @RequestBody SavingGoalsCreateRequest req){
        return ResponseEntity.ok(
                ApiResponse.<SavingGoalsResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(savingGoalsService.update(req, id))
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<SavingGoalsResponse>>> getAll(){
        return ResponseEntity.ok(
                ApiResponse.<List<SavingGoalsResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(savingGoalsService.getAll())
                        .total(savingGoalsService.getAll().size())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SavingGoalsResponse>> getDetails(@PathVariable Long id){
        return ResponseEntity.ok(
                ApiResponse.<SavingGoalsResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(savingGoalsService.getDetails(id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SavingGoalsResponse>> delete(@PathVariable Long id){
        savingGoalsService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<SavingGoalsResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .build()
        );
    }


}
