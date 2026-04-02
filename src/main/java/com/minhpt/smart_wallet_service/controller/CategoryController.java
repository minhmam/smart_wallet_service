package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.CategoryCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> search(@RequestBody CategorySearchRequest req) {
        Page<CategoryResponse> page = categoryService.search(req);
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(page.getContent())
                        .total(page.getTotalElements())
                        .build()
        );
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest req) {
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(categoryService.create(req))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id,
                                                                        @Valid @RequestBody CategoryCreateRequest req) {
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(categoryService.update(req, id))
                        .build()
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getDetail(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(categoryService.getDetail(categoryId))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .build()
        );
    }

    @PutMapping("/pin")
    public ResponseEntity<Object> changePin(@RequestParam Long id) {
        categoryService.changePin(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .build()
        );
    }
}
