package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.model.SubscriptionPlan;

import java.util.List;

public interface SubscriptionPlanService {

    List<SubscriptionPlan> getAll();

    SubscriptionPlanResponse getDetail(Long id);
}
