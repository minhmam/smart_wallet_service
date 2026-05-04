package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanRequest;
import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.model.SubscriptionPlan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SubscriptionPlanService {

    List<SubscriptionPlan> getAll();

    SubscriptionPlanResponse getDetail(Long id);

    SubscriptionPlanResponse create(SubscriptionPlanRequest request);

    SubscriptionPlanResponse update(Long id, SubscriptionPlanRequest request);

    Page<SubscriptionPlanResponse> search(SubscriptionPlanSearchRequest request);

    void delete(Long id);
}
