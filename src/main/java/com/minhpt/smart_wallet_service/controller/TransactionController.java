package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JsonMapper.Builder builder;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> search(
            @RequestBody TransactionSearchRequest req
    ) {
        Page<TransactionResponse> page = transactionService.search(req);
        return ResponseEntity.ok(ApiResponse.successPage(page.getContent(), page.getTotalElements()));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<TransactionResponse>> create(@Valid @RequestBody TransactionCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(@PathVariable Long id,
                                                                   @Valid @RequestBody TransactionCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.update(req, id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getDetail(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
