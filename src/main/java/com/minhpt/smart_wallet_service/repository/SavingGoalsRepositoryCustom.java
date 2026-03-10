package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import org.springframework.data.domain.Page;


public interface SavingGoalsRepositoryCustom {
    Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req);
}
