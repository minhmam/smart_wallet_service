package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationScheduleResponse;
import org.springframework.data.domain.Page;

public interface NotificationScheduleRepositoryCustom {
    Page<NotificationScheduleResponse> search(NotificationScheduleSearchRequest req);
}
