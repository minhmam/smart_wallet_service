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

    @GetMapping("/top-5/get")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getTop5MostUsedCategories(){
        List<CategoryResponse> categories = categoryService.getTop5MostUsedCategories();
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder().
                        status(200)
                        .message(Constant.SUCCESS)
                        .data(categories)
                        .total(categories.size())
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> search(@RequestBody CategorySearchRequest req){
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

    @PostMapping("")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest req){
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(categoryService.create(req))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryCreateRequest req){
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(categoryService.update(req, id))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(){
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(categoryService.getAll())
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
