package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.mapper.SubscriptionPlanMapper;
import com.minhpt.smart_wallet_service.model.SubscriptionPlan;
import com.minhpt.smart_wallet_service.repository.SubscriptionPlanRepository;
import com.minhpt.smart_wallet_service.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    @Override
    public List<SubscriptionPlan> getAll() {
        return subscriptionPlanRepository.findAllByStatus(Constant.NOT_DELETE);
    }

    @Override
    public SubscriptionPlanResponse getDetail(Long id) {

        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found by id " + id));

        return subscriptionPlanMapper.toResponse(subscriptionPlan);
    }
}

