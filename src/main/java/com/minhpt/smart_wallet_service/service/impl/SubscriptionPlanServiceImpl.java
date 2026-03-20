package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.model.SubcriptionPlan;
import com.minhpt.smart_wallet_service.repository.SubcriptionPlanRepo;
import com.minhpt.smart_wallet_service.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubcriptionPlanRepo subcriptionPlanRepo;

    @Override
    public List<SubcriptionPlan> getAll() {

        return subcriptionPlanRepo.findAllByStatus(Constant.NOT_DELETE);
    }
}
