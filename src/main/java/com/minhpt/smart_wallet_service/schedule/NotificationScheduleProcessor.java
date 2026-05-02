package com.minhpt.smart_wallet_service.schedule;

import com.minhpt.smart_wallet_service.service.NotificationScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduleProcessor {

    private final NotificationScheduleService notificationScheduleService;

    @Scheduled(fixedDelayString = "${app.notifications.poll-ms:60000}")
    public void processDueSchedules() {
        log.debug("Processing due notification schedules");
        notificationScheduleService.processDueSchedules();
    }
}
