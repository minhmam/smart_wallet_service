package com.minhpt.smart_wallet_service.schedule;

import com.minhpt.smart_wallet_service.service.ScheduledActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledActionProcessor {

    private final ScheduledActionService scheduledActionService;

    @Scheduled(fixedDelayString = "${app.scheduled-actions.poll-ms:60000}")
    public void processDueSchedules() {
        log.debug("Processing due scheduled actions");
        scheduledActionService.processDueSchedules();
    }
}
