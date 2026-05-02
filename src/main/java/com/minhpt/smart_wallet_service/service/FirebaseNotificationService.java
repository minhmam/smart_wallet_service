package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.FirebasePushResult;
import com.minhpt.smart_wallet_service.model.NotificationSchedule;
import com.minhpt.smart_wallet_service.model.UserNotificationToken;

import java.util.List;

public interface FirebaseNotificationService {
    FirebasePushResult send(NotificationSchedule schedule, List<UserNotificationToken> tokens);
}
