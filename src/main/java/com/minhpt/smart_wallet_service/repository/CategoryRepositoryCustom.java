package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;

import java.util.Map;

public interface CategoryRepositoryCustom {
    Map<String, Object> search(CategorySearchRequest request);
}
