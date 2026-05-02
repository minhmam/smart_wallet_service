package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleSearchRequest;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationScheduleResponse;
import org.springframework.data.domain.Page;

public interface NotificationScheduleService {

    NotificationScheduleResponse create(NotificationScheduleCreateRequest request);

    Page<NotificationScheduleResponse> search(NotificationScheduleSearchRequest request);

    NotificationScheduleResponse getDetail(Long id);

    NotificationScheduleResponse update(Long id, NotificationScheduleUpdateRequest request);

    NotificationScheduleResponse disable(Long id);

    NotificationScheduleResponse enable(Long id);

    NotificationScheduleResponse delete(Long id);

    void processDueSchedules();
}
