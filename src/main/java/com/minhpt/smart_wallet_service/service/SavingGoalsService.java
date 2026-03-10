package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SavingGoalsService {
    SavingGoalsResponse create(SavingGoalsCreateRequest req);

    SavingGoalsResponse update(SavingGoalsCreateRequest req, Long id);

    List<SavingGoalsResponse> getAll();

    SavingGoalsResponse getDetails(Long id);

    void delete(Long id);

    Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req);
}
