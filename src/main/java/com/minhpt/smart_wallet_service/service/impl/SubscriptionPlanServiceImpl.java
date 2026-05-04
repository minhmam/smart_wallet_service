package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanRequest;
import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.SubscriptionPlanMapper;
import com.minhpt.smart_wallet_service.model.SubscriptionPlan;
import com.minhpt.smart_wallet_service.repository.SubscriptionPlanRepository;
import com.minhpt.smart_wallet_service.service.SubscriptionPlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found by id = " + id));

        return subscriptionPlanMapper.toResponse(subscriptionPlan);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse create(SubscriptionPlanRequest request) {
        validateRequest(request);
        String code = normalizeText(request.getCode(), "Code");

        if (subscriptionPlanRepository.existsByCodeIgnoreCaseAndStatus(code, Constant.NOT_DELETE)) {
            throw new IllegalArgumentException("Subscription plan code already exists");
        }

        SubscriptionPlan subscriptionPlan = SubscriptionPlan.builder()
                .code(code)
                .name(normalizeText(request.getName(), "Name"))
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .description(normalizeNullable(request.getDescription()))
                .build();

        return subscriptionPlanMapper.toResponse(subscriptionPlanRepository.save(subscriptionPlan));
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse update(Long id, SubscriptionPlanRequest request) {
        validateRequest(request);
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found by id = " + id));

        String code = normalizeText(request.getCode(), "Code");
        if (subscriptionPlanRepository.existsByCodeIgnoreCaseAndIdNotAndStatus(code, id, Constant.NOT_DELETE)) {
            throw new IllegalArgumentException("Subscription plan code already exists");
        }

        subscriptionPlan.setCode(code);
        subscriptionPlan.setName(normalizeText(request.getName(), "Name"));
        subscriptionPlan.setPrice(request.getPrice());
        subscriptionPlan.setDurationDays(request.getDurationDays());
        subscriptionPlan.setDescription(normalizeNullable(request.getDescription()));

        return subscriptionPlanMapper.toResponse(subscriptionPlanRepository.save(subscriptionPlan));
    }

    @Override
    public Page<SubscriptionPlanResponse> search(SubscriptionPlanSearchRequest request) {
        return subscriptionPlanRepository.search(request);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found by id = " + id));

        subscriptionPlan.setStatus(Constant.DELETED);
        subscriptionPlanRepository.save(subscriptionPlan);
    }

    private void validateRequest(SubscriptionPlanRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Subscription plan request must not be null");
        }
    }

    private String normalizeText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return value.trim();
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
