package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CategoryRepositoryCustom {
    Page<CategoryResponse> search(CategorySearchRequest request);
}
