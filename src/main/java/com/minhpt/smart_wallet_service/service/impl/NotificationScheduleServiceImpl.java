package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleSearchRequest;
import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.FirebasePushResult;
import com.minhpt.smart_wallet_service.dto.response.NotificationScheduleResponse;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.model.NotificationSchedule;
import com.minhpt.smart_wallet_service.model.UserNotificationToken;
import com.minhpt.smart_wallet_service.repository.NotificationScheduleRepository;
import com.minhpt.smart_wallet_service.repository.UserNotificationTokenRepository;
import com.minhpt.smart_wallet_service.service.FirebaseNotificationService;
import com.minhpt.smart_wallet_service.service.NotificationScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NotificationScheduleServiceImpl implements NotificationScheduleService {

    private final NotificationScheduleRepository notificationScheduleRepository;
    private final UserNotificationTokenRepository userNotificationTokenRepository;
    private final FirebaseNotificationService firebaseNotificationService;

    @Override
    @Transactional
    public NotificationScheduleResponse create(NotificationScheduleCreateRequest request) {
        LocalDateTime startAt = resolveStartAt(request);
        NotificationSchedule schedule = NotificationSchedule.builder()
                .title(normalizeText(request.getTitle(), "Title"))
                .content(normalizeText(request.getContent(), "Content"))
                .targetGroup(normalizeTargetGroup(request.getTargetGroup()))
                .recurrenceType(normalizeRecurrenceType(request.getRecurrenceType()))
                .scheduleState(Constant.SCHEDULE_STATE_PENDING)
                .startAt(startAt)
                .nextRunAt(startAt)
                .lastTargetUserCount(0L)
                .lastSuccessCount(0L)
                .lastFailureCount(0L)
                .retryCount(0)
                .build();

        NotificationSchedule savedSchedule = notificationScheduleRepository.save(schedule);

        if (Boolean.TRUE.equals(request.getSendNow())) {
            processSchedule(savedSchedule.getId());
            savedSchedule = notificationScheduleRepository.findById(savedSchedule.getId()).orElse(savedSchedule);
        }

        return toResponse(savedSchedule);
    }

    @Override
    public Page<NotificationScheduleResponse> search(NotificationScheduleSearchRequest request) {
        NotificationScheduleSearchRequest searchRequest = request == null
                ? new NotificationScheduleSearchRequest()
                : request;
        return notificationScheduleRepository.search(searchRequest);
    }

    @Override
    public NotificationScheduleResponse getDetail(Long id) {
        NotificationSchedule schedule = getActiveSchedule(id);
        return toResponse(schedule);
    }

    @Override
    @Transactional
    public NotificationScheduleResponse update(Long id, NotificationScheduleUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request must not be null");
        }

        NotificationSchedule schedule = getActiveSchedule(id);
        validateUpdateableSchedule(schedule);

        boolean hasChanges = false;

        if (request.getTitle() != null) {
            schedule.setTitle(normalizeText(request.getTitle(), "Title"));
            hasChanges = true;
        }

        if (request.getContent() != null) {
            schedule.setContent(normalizeText(request.getContent(), "Content"));
            hasChanges = true;
        }

        if (request.getTargetGroup() != null) {
            schedule.setTargetGroup(normalizeTargetGroup(request.getTargetGroup()));
            hasChanges = true;
        }

        if (request.getRecurrenceType() != null) {
            schedule.setRecurrenceType(normalizeRecurrenceType(request.getRecurrenceType()));
            hasChanges = true;
        }

        if (request.getSendAt() != null) {
            schedule.setStartAt(request.getSendAt());
            schedule.setNextRunAt(resolveEnabledNextRunAt(request.getSendAt()));
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        if (Constant.SCHEDULE_STATE_PROCESSING.equals(schedule.getScheduleState())) {
            schedule.setScheduleState(Constant.SCHEDULE_STATE_PENDING);
        }

        return toResponse(notificationScheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public NotificationScheduleResponse disable(Long id) {
        NotificationSchedule schedule = getActiveSchedule(id);

        if (Constant.SCHEDULE_STATE_COMPLETED.equals(schedule.getScheduleState())) {
            throw new IllegalArgumentException("Completed notification schedule cannot be disabled");
        }

        schedule.setScheduleState(Constant.SCHEDULE_STATE_CANCELED);
        schedule.setNextRunAt(null);

        return toResponse(notificationScheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public NotificationScheduleResponse enable(Long id) {
        NotificationSchedule schedule = getActiveSchedule(id);

        if (!Constant.SCHEDULE_STATE_CANCELED.equals(schedule.getScheduleState())
                && !Constant.SCHEDULE_STATE_FAILED.equals(schedule.getScheduleState())) {
            throw new IllegalArgumentException("Only canceled or failed notification schedules can be enabled");
        }

        schedule.setScheduleState(Constant.SCHEDULE_STATE_PENDING);
        schedule.setNextRunAt(resolveEnabledNextRunAt(schedule.getStartAt()));
        schedule.setFailureReason(null);

        return toResponse(notificationScheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public NotificationScheduleResponse delete(Long id) {
        NotificationSchedule schedule = getActiveSchedule(id);

        schedule.setStatus(Constant.DELETED);
        schedule.setScheduleState(Constant.SCHEDULE_STATE_CANCELED);
        schedule.setNextRunAt(null);

        return toResponse(notificationScheduleRepository.save(schedule));
    }

    @Override
    public void processDueSchedules() {
        List<NotificationSchedule> schedules = notificationScheduleRepository
                .findTop50ByScheduleStateAndNextRunAtLessThanEqualAndStatusOrderByNextRunAtAsc(
                        Constant.SCHEDULE_STATE_PENDING,
                        LocalDateTime.now(),
                        Constant.NOT_DELETE
                );

        for (NotificationSchedule schedule : schedules) {
            processSchedule(schedule.getId());
        }
    }

    @Transactional
    public void processSchedule(Long scheduleId) {
        NotificationSchedule schedule = notificationScheduleRepository.findById(scheduleId).orElse(null);

        if (schedule == null
                || !Constant.SCHEDULE_STATE_PENDING.equals(schedule.getScheduleState())
                || schedule.getNextRunAt() == null
                || schedule.getNextRunAt().isAfter(LocalDateTime.now())) {
            return;
        }

        LocalDateTime currentRunAt = schedule.getNextRunAt();
        schedule.setScheduleState(Constant.SCHEDULE_STATE_PROCESSING);
        notificationScheduleRepository.save(schedule);

        try {
            List<UserNotificationToken> targetTokens = userNotificationTokenRepository.findActiveTokensByTargetGroup(
                    schedule.getTargetGroup(),
                    Constant.NOT_DELETE,
                    LocalDateTime.now()
            );
            FirebasePushResult result = firebaseNotificationService.send(schedule, targetTokens);
            finalizeRun(schedule, result, null, currentRunAt);
        } catch (RuntimeException ex) {
            finalizeRun(schedule, FirebasePushResult.builder()
                    .targetUserCount(0L)
                    .successCount(0L)
                    .failureCount(0L)
                    .build(), ex.getMessage(), currentRunAt);
        }
    }

    private void finalizeRun(
            NotificationSchedule schedule,
            FirebasePushResult result,
            String failureReason,
            LocalDateTime currentRunAt
    ) {
        boolean failed = failureReason != null && !failureReason.isBlank();
        LocalDateTime nextRunAt = failed ? null : calculateNextRunAt(schedule, currentRunAt);

        schedule.setLastRunAt(LocalDateTime.now());
        schedule.setLastExecutionStatus(failed ? Constant.SCHEDULE_EXECUTION_FAILED : Constant.SCHEDULE_EXECUTION_SUCCESS);
        schedule.setLastTargetUserCount(result.getTargetUserCount());
        schedule.setLastSuccessCount(result.getSuccessCount());
        schedule.setLastFailureCount(result.getFailureCount());
        schedule.setFailureReason(failed ? truncateMessage(failureReason) : null);

        if (failed) {
            Integer retryCount = schedule.getRetryCount() == null ? 0 : schedule.getRetryCount();
            schedule.setRetryCount(retryCount + 1);
            schedule.setNextRunAt(null);
            schedule.setScheduleState(Constant.SCHEDULE_STATE_FAILED);
        } else if (nextRunAt == null) {
            schedule.setNextRunAt(null);
            schedule.setScheduleState(Constant.SCHEDULE_STATE_COMPLETED);
        } else {
            schedule.setNextRunAt(nextRunAt);
            schedule.setScheduleState(Constant.SCHEDULE_STATE_PENDING);
        }

        notificationScheduleRepository.save(schedule);
    }

    private LocalDateTime calculateNextRunAt(NotificationSchedule schedule, LocalDateTime currentRunAt) {
        if (Constant.NOTIFICATION_RECURRENCE_ONCE.equals(schedule.getRecurrenceType())) {
            return null;
        }

        if (Constant.NOTIFICATION_RECURRENCE_DAILY.equals(schedule.getRecurrenceType())) {
            return currentRunAt.plusDays(1);
        }

        if (Constant.NOTIFICATION_RECURRENCE_WEEKLY.equals(schedule.getRecurrenceType())) {
            return currentRunAt.plusWeeks(1);
        }

        if (Constant.NOTIFICATION_RECURRENCE_MONTHLY.equals(schedule.getRecurrenceType())) {
            return currentRunAt.plusMonths(1);
        }

        throw new IllegalArgumentException("Unsupported notification recurrence type: " + schedule.getRecurrenceType());
    }

    private LocalDateTime resolveStartAt(NotificationScheduleCreateRequest request) {
        if (Boolean.TRUE.equals(request.getSendNow())) {
            return LocalDateTime.now();
        }

        if (request.getSendAt() == null) {
            throw new IllegalArgumentException("Send time is required");
        }

        return request.getSendAt();
    }

    private LocalDateTime resolveEnabledNextRunAt(LocalDateTime startAt) {
        if (startAt == null || startAt.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }

        return startAt;
    }

    private NotificationSchedule getActiveSchedule(Long id) {
        return notificationScheduleRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Notification schedule not found by ID = " + id));
    }

    private void validateUpdateableSchedule(NotificationSchedule schedule) {
        if (Constant.SCHEDULE_STATE_PENDING.equals(schedule.getScheduleState())
                || Constant.SCHEDULE_STATE_PROCESSING.equals(schedule.getScheduleState())) {
            return;
        }

        throw new IllegalArgumentException("Only pending or processing notification schedules can be updated");
    }

    private String normalizeTargetGroup(String targetGroup) {
        String normalizedValue = normalizeText(targetGroup, "Target group").toUpperCase(Locale.ROOT);
        if (Constant.NOTIFICATION_TARGET_ALL.equals(normalizedValue)
                || Constant.NOTIFICATION_TARGET_PREMIUM.equals(normalizedValue)
                || Constant.NOTIFICATION_TARGET_FREE.equals(normalizedValue)) {
            return normalizedValue;
        }

        throw new IllegalArgumentException("Target group must be ALL, PREMIUM or FREE");
    }

    private String normalizeRecurrenceType(String recurrenceType) {
        String normalizedValue = normalizeText(recurrenceType, "Recurrence type").toUpperCase(Locale.ROOT);
        if (Constant.NOTIFICATION_RECURRENCE_ONCE.equals(normalizedValue)
                || Constant.NOTIFICATION_RECURRENCE_DAILY.equals(normalizedValue)
                || Constant.NOTIFICATION_RECURRENCE_WEEKLY.equals(normalizedValue)
                || Constant.NOTIFICATION_RECURRENCE_MONTHLY.equals(normalizedValue)) {
            return normalizedValue;
        }

        throw new IllegalArgumentException("Recurrence type must be ONCE, DAILY, WEEKLY or MONTHLY");
    }

    private String normalizeText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return value.trim();
    }

    private String truncateMessage(String message) {
        if (message == null || message.isBlank()) {
            return "Unknown error";
        }

        return message.length() > 500 ? message.substring(0, 500) : message;
    }

    private NotificationScheduleResponse toResponse(NotificationSchedule schedule) {
        return NotificationScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .targetGroup(schedule.getTargetGroup())
                .scheduleState(schedule.getScheduleState())
                .recurrenceType(schedule.getRecurrenceType())
                .startAt(schedule.getStartAt())
                .nextRunAt(schedule.getNextRunAt())
                .lastRunAt(schedule.getLastRunAt())
                .lastExecutionStatus(schedule.getLastExecutionStatus())
                .lastTargetUserCount(schedule.getLastTargetUserCount())
                .lastSuccessCount(schedule.getLastSuccessCount())
                .lastFailureCount(schedule.getLastFailureCount())
                .failureReason(schedule.getFailureReason())
                .retryCount(schedule.getRetryCount())
                .createdAt(schedule.getCreatedAt())
                .build();
    }
}
