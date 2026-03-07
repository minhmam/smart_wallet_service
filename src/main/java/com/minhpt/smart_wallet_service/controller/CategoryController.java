package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.CategoryCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.service.CategoryServiceCustom;
import com.minhpt.smart_wallet_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> search(@RequestBody CategorySearchRequest req){
        Map<String, Object> map = categoryService.search(req);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(map)
                        .build()
        );
    }

    @PostMapping("/")
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
