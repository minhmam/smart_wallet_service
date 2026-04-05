package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.ScheduledGoalDepositCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.ScheduledTransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.ScheduledActionResponse;

public interface ScheduledActionService {

    ScheduledActionResponse createTransactionSchedule(ScheduledTransactionCreateRequest request);

    ScheduledActionResponse createGoalDepositSchedule(ScheduledGoalDepositCreateRequest request);

    ScheduledActionResponse getDetail(Long id);

    ScheduledActionResponse cancel(Long id);

    void processDueSchedules();
}
