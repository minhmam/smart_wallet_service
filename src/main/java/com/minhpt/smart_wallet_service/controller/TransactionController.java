package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
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
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> search(@RequestBody TransactionSearchRequest req){
        Page<TransactionResponse> page = transactionService.search(req);
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(page.getContent())
                        .total(page.getTotalElements())
                        .build()
        );
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<TransactionResponse>> create(@Valid @RequestBody TransactionCreateRequest req) {
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(transactionService.create(req))
                        .build()

        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(@PathVariable Long id, @Valid @RequestBody TransactionCreateRequest req) {
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(transactionService.update(req, id))
                        .build()

        );
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAll(){
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(transactionService.getAll())
                        .total(transactionService.getAll().size())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getDetailsTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(transactionService.getDetailsTransaction(id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .build()
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findByType(@PathVariable String type){
        List<TransactionResponse> transactionResponseList = transactionService.findByType(type);
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponse>> builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(transactionResponseList)
                        .total(transactionResponseList.size())
                        .build()
        );
    }
}

