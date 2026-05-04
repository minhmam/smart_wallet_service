package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import org.springframework.data.domain.Page;

public interface SubscriptionPlanRepositoryCustom {
    Page<SubscriptionPlanResponse> search(SubscriptionPlanSearchRequest request);
}
