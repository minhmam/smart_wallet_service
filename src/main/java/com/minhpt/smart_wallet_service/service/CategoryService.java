package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.CategoryCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    CategoryResponse create(CategoryCreateRequest req);

    CategoryResponse update(CategoryCreateRequest req, long id);

    void delete(Long id);

    List<CategoryResponse> getAll();

    CategoryResponse getById();

    void changePin(Long id);

    Page<CategoryResponse> search(CategorySearchRequest request);

}
