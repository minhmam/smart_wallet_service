package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface SavingGoalsService {
    Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req);

    SavingGoalsResponse create(SavingGoalsCreateRequest req);

    SavingGoalsResponse update(SavingGoalsCreateRequest req, Long id);

    SavingGoalsResponse getDetails(Long id);

    void delete(Long id);

    SavingGoalsResponse deposit(Long id, BigDecimal amount);
}
