package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.ScheduledGoalDepositCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.ScheduledTransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.ScheduledActionResponse;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.model.ScheduledAction;
import com.minhpt.smart_wallet_service.model.SavingGoal;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.CategoryRepository;
import com.minhpt.smart_wallet_service.repository.ScheduledActionRepository;
import com.minhpt.smart_wallet_service.repository.SavingGoalsRepository;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.SavingGoalsService;
import com.minhpt.smart_wallet_service.service.ScheduledActionService;
import com.minhpt.smart_wallet_service.service.TransactionService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledActionServiceImpl implements ScheduledActionService {

    private final ScheduledActionRepository scheduledActionRepository;
    private final TransactionService transactionService;
    private final SavingGoalsService savingGoalsService;
    private final AuthenticationUtil authenticationUtil;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SavingGoalsRepository savingGoalsRepository;

    @Override
    @Transactional
    public ScheduledActionResponse createTransactionSchedule(ScheduledTransactionCreateRequest request) {
        User loginUser = getCurrentUser();
        categoryRepository.findByIdAndStatus(request.getCategoryId(), Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by ID = " + request.getCategoryId()));

        ScheduleSetup scheduleSetup = buildScheduleSetup(
                request.getStartAt(),
                request.getEndAt(),
                request.getRecurrenceType(),
                request.getIntervalValue(),
                request.getDaysOfWeek(),
                request.getDayOfMonth()
        );

        ScheduledAction scheduledAction = ScheduledAction.builder()
                .userId(loginUser.getId())
                .actionType(Constant.SCHEDULE_ACTION_TRANSACTION)
                .scheduleState(Constant.SCHEDULE_STATE_PENDING)
                .recurrenceType(scheduleSetup.recurrenceType())
                .intervalValue(scheduleSetup.intervalValue())
                .startAt(request.getStartAt())
                .nextRunAt(scheduleSetup.firstRunAt())
                .endAt(request.getEndAt())
                .daysOfWeek(scheduleSetup.daysOfWeekCsv())
                .dayOfMonth(scheduleSetup.dayOfMonth())
                .amount(request.getAmount())
                .transactionType(request.getType())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .retryCount(0)
                .build();

        return toResponse(scheduledActionRepository.save(scheduledAction));
    }

    @Override
    @Transactional
    public ScheduledActionResponse createGoalDepositSchedule(ScheduledGoalDepositCreateRequest request) {
        User loginUser = getCurrentUser();

        SavingGoal savingGoal = savingGoalsRepository.findByIdAndStatus(request.getSavingGoalId(), Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found by ID = " + request.getSavingGoalId()));
        validateSavingGoalOwner(savingGoal, loginUser);

        ScheduleSetup scheduleSetup = buildScheduleSetup(
                request.getStartAt(),
                request.getEndAt(),
                request.getRecurrenceType(),
                request.getIntervalValue(),
                request.getDaysOfWeek(),
                request.getDayOfMonth()
        );

        ScheduledAction scheduledAction = ScheduledAction.builder()
                .userId(loginUser.getId())
                .actionType(Constant.SCHEDULE_ACTION_GOAL_DEPOSIT)
                .scheduleState(Constant.SCHEDULE_STATE_PENDING)
                .recurrenceType(scheduleSetup.recurrenceType())
                .intervalValue(scheduleSetup.intervalValue())
                .startAt(request.getStartAt())
                .nextRunAt(scheduleSetup.firstRunAt())
                .endAt(request.getEndAt())
                .daysOfWeek(scheduleSetup.daysOfWeekCsv())
                .dayOfMonth(scheduleSetup.dayOfMonth())
                .amount(request.getAmount())
                .savingGoalId(request.getSavingGoalId())
                .retryCount(0)
                .build();

        return toResponse(scheduledActionRepository.save(scheduledAction));
    }

    @Override
    public ScheduledActionResponse getDetail(Long id) {
        User loginUser = getCurrentUser();
        ScheduledAction scheduledAction = scheduledActionRepository.findByIdAndUserIdAndStatus(id, loginUser.getId(), Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled action not found by ID = " + id));
        return toResponse(scheduledAction);
    }

    @Override
    @Transactional
    public ScheduledActionResponse cancel(Long id) {
        User loginUser = getCurrentUser();
        ScheduledAction scheduledAction = scheduledActionRepository.findByIdAndUserIdAndStatus(id, loginUser.getId(), Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled action not found by ID = " + id));

        if (!Constant.SCHEDULE_STATE_PENDING.equals(scheduledAction.getScheduleState())) {
            throw new IllegalArgumentException("Only pending schedules can be canceled");
        }

        scheduledAction.setScheduleState(Constant.SCHEDULE_STATE_CANCELED);

        return toResponse(scheduledActionRepository.save(scheduledAction));
    }

    @Override
    public void processDueSchedules() {
        List<ScheduledAction> scheduledActions = scheduledActionRepository
                .findTop50ByScheduleStateAndNextRunAtLessThanEqualAndStatusOrderByNextRunAtAsc(
                        Constant.SCHEDULE_STATE_PENDING,
                        LocalDateTime.now(),
                        Constant.NOT_DELETE
                );

        for (ScheduledAction scheduledAction : scheduledActions) {
            processScheduledAction(scheduledAction.getId());
        }
    }

    @Transactional
    public void processScheduledAction(Long scheduledActionId) {
        ScheduledAction scheduledAction = scheduledActionRepository.findById(scheduledActionId)
                .orElse(null);

        if (scheduledAction == null
                || !Constant.SCHEDULE_STATE_PENDING.equals(scheduledAction.getScheduleState())
                || scheduledAction.getNextRunAt() == null
                || scheduledAction.getNextRunAt().isAfter(LocalDateTime.now())) {
            return;
        }

        User user = userRepository.findByIdAndStatus(scheduledAction.getUserId(), Constant.NOT_DELETE)
                .orElse(null);

        if (user == null) {
            markPermanentFailure(scheduledAction, "User not found or inactive");
            return;
        }

        LocalDateTime currentRunAt = scheduledAction.getNextRunAt();

        scheduledAction.setScheduleState(Constant.SCHEDULE_STATE_PROCESSING);
        scheduledActionRepository.save(scheduledAction);

        try {
            runAsUser(user, () -> {
                executeScheduledAction(scheduledAction, currentRunAt);
                return null;
            });
            finalizeRun(scheduledAction, Constant.SCHEDULE_EXECUTION_SUCCESS, null, currentRunAt);
        } catch (RuntimeException ex) {
            finalizeRun(scheduledAction, Constant.SCHEDULE_EXECUTION_FAILED, ex.getMessage(), currentRunAt);
        }
    }

    private void executeScheduledAction(ScheduledAction scheduledAction, LocalDateTime currentRunAt) {
        if (Constant.SCHEDULE_ACTION_TRANSACTION.equals(scheduledAction.getActionType())) {
            TransactionCreateRequest request = TransactionCreateRequest.builder()
                    .amount(scheduledAction.getAmount())
                    .type(scheduledAction.getTransactionType())
                    .description(scheduledAction.getDescription())
                    .categoryId(scheduledAction.getCategoryId())
                    .transactionDate(currentRunAt)
                    .aiPredicted(Boolean.FALSE)
                    .build();
            transactionService.create(request);
            return;
        }

        if (Constant.SCHEDULE_ACTION_GOAL_DEPOSIT.equals(scheduledAction.getActionType())) {
            savingGoalsService.deposit(scheduledAction.getSavingGoalId(), scheduledAction.getAmount());
            return;
        }

        throw new IllegalArgumentException("Unsupported schedule action: " + scheduledAction.getActionType());
    }

    private void finalizeRun(
            ScheduledAction scheduledAction,
            String executionStatus,
            String failureReason,
            LocalDateTime currentRunAt
    ) {
        LocalDateTime processedAt = LocalDateTime.now();
        LocalDateTime nextRunAt = calculateNextRunAt(scheduledAction, currentRunAt);

        scheduledAction.setLastRunAt(processedAt);
        scheduledAction.setLastExecutionStatus(executionStatus);
        scheduledAction.setFailureReason(Constant.SCHEDULE_EXECUTION_FAILED.equals(executionStatus)
                ? truncateMessage(failureReason)
                : null);

        if (Constant.SCHEDULE_EXECUTION_FAILED.equals(executionStatus)) {
            Integer retryCount = scheduledAction.getRetryCount() == null ? 0 : scheduledAction.getRetryCount();
            scheduledAction.setRetryCount(retryCount + 1);
        }

        if (nextRunAt == null) {
            scheduledAction.setNextRunAt(null);
            scheduledAction.setScheduleState(Constant.SCHEDULE_STATE_COMPLETED);
        } else {
            scheduledAction.setNextRunAt(nextRunAt);
            scheduledAction.setScheduleState(Constant.SCHEDULE_STATE_PENDING);
        }

        scheduledActionRepository.save(scheduledAction);
    }

    private void markPermanentFailure(ScheduledAction scheduledAction, String message) {
        Integer retryCount = scheduledAction.getRetryCount() == null ? 0 : scheduledAction.getRetryCount();
        scheduledAction.setRetryCount(retryCount + 1);
        scheduledAction.setScheduleState(Constant.SCHEDULE_STATE_FAILED);
        scheduledAction.setLastExecutionStatus(Constant.SCHEDULE_EXECUTION_FAILED);
        scheduledAction.setLastRunAt(LocalDateTime.now());
        scheduledAction.setFailureReason(truncateMessage(message));
        scheduledActionRepository.save(scheduledAction);
    }

    private ScheduleSetup buildScheduleSetup(
            LocalDateTime startAt,
            LocalDateTime endAt,
            String recurrenceType,
            Integer intervalValue,
            List<String> requestDaysOfWeek,
            Integer requestDayOfMonth
    ) {
        if (startAt == null) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (endAt != null && endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("End time must be greater than or equal to start time");
        }

        String normalizedRecurrenceType = normalizeRecurrenceType(recurrenceType);
        int resolvedIntervalValue = intervalValue == null ? 1 : intervalValue;
        if (resolvedIntervalValue < 1) {
            throw new IllegalArgumentException("Interval value must be greater than or equal to 1");
        }

        String daysOfWeekCsv = null;
        Integer resolvedDayOfMonth = null;
        LocalDateTime firstRunAt;

        if (Constant.SCHEDULE_FREQUENCY_DAILY.equals(normalizedRecurrenceType)) {
            firstRunAt = startAt;
        } else if (Constant.SCHEDULE_FREQUENCY_WEEKLY.equals(normalizedRecurrenceType)) {
            Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(requestDaysOfWeek, startAt.getDayOfWeek());
            daysOfWeekCsv = toDaysOfWeekCsv(daysOfWeek);
            firstRunAt = calculateFirstWeeklyRunAt(startAt, daysOfWeek, resolvedIntervalValue);
        } else {
            resolvedDayOfMonth = requestDayOfMonth == null ? startAt.getDayOfMonth() : requestDayOfMonth;
            if (resolvedDayOfMonth < 1 || resolvedDayOfMonth > 31) {
                throw new IllegalArgumentException("Day of month must be between 1 and 31");
            }
            firstRunAt = calculateFirstMonthlyRunAt(startAt, resolvedDayOfMonth, resolvedIntervalValue);
        }

        if (endAt != null && firstRunAt.isAfter(endAt)) {
            throw new IllegalArgumentException("No schedule occurrence is available before end time");
        }

        return new ScheduleSetup(
                normalizedRecurrenceType,
                resolvedIntervalValue,
                daysOfWeekCsv,
                resolvedDayOfMonth,
                firstRunAt
        );
    }

    private LocalDateTime calculateNextRunAt(ScheduledAction scheduledAction, LocalDateTime currentRunAt) {
        LocalDateTime nextRunAt;

        if (Constant.SCHEDULE_FREQUENCY_DAILY.equals(scheduledAction.getRecurrenceType())) {
            nextRunAt = currentRunAt.plusDays(scheduledAction.getIntervalValue());
        } else if (Constant.SCHEDULE_FREQUENCY_WEEKLY.equals(scheduledAction.getRecurrenceType())) {
            nextRunAt = calculateNextWeeklyRunAt(scheduledAction, currentRunAt);
        } else if (Constant.SCHEDULE_FREQUENCY_MONTHLY.equals(scheduledAction.getRecurrenceType())) {
            nextRunAt = calculateNextMonthlyRunAt(scheduledAction, currentRunAt);
        } else {
            throw new IllegalArgumentException("Unsupported recurrence type: " + scheduledAction.getRecurrenceType());
        }

        if (scheduledAction.getEndAt() != null && nextRunAt.isAfter(scheduledAction.getEndAt())) {
            return null;
        }

        return nextRunAt;
    }

    private LocalDateTime calculateFirstWeeklyRunAt(LocalDateTime startAt, Set<DayOfWeek> daysOfWeek, int intervalValue) {
        List<DayOfWeek> sortedDays = sortDaysOfWeek(daysOfWeek);
        int currentDayValue = startAt.getDayOfWeek().getValue();

        for (DayOfWeek dayOfWeek : sortedDays) {
            if (dayOfWeek.getValue() >= currentDayValue) {
                return startAt.plusDays(dayOfWeek.getValue() - currentDayValue);
            }
        }

        LocalDate weekStart = startAt.toLocalDate().minusDays(currentDayValue - 1L);
        LocalDate nextWeekStart = weekStart.plusWeeks(intervalValue);
        DayOfWeek firstDay = sortedDays.get(0);
        return nextWeekStart
                .plusDays(firstDay.getValue() - 1L)
                .atTime(startAt.toLocalTime());
    }

    private LocalDateTime calculateNextWeeklyRunAt(ScheduledAction scheduledAction, LocalDateTime currentRunAt) {
        Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(splitDaysOfWeek(scheduledAction.getDaysOfWeek()), currentRunAt.getDayOfWeek());
        List<DayOfWeek> sortedDays = sortDaysOfWeek(daysOfWeek);
        int currentDayValue = currentRunAt.getDayOfWeek().getValue();

        for (DayOfWeek dayOfWeek : sortedDays) {
            if (dayOfWeek.getValue() > currentDayValue) {
                return currentRunAt.plusDays(dayOfWeek.getValue() - currentDayValue);
            }
        }

        LocalDate weekStart = currentRunAt.toLocalDate().minusDays(currentDayValue - 1L);
        LocalDate nextWeekStart = weekStart.plusWeeks(scheduledAction.getIntervalValue());
        DayOfWeek firstDay = sortedDays.get(0);
        return nextWeekStart
                .plusDays(firstDay.getValue() - 1L)
                .atTime(currentRunAt.toLocalTime());
    }

    private LocalDateTime calculateFirstMonthlyRunAt(LocalDateTime startAt, int dayOfMonth, int intervalValue) {
        LocalDateTime candidate = buildMonthlyRunAt(startAt, startAt.getYear(), startAt.getMonthValue(), dayOfMonth);
        if (!candidate.isBefore(startAt)) {
            return candidate;
        }

        LocalDateTime nextMonthCandidate = startAt.plusMonths(intervalValue);
        return buildMonthlyRunAt(nextMonthCandidate, nextMonthCandidate.getYear(), nextMonthCandidate.getMonthValue(), dayOfMonth);
    }

    private LocalDateTime calculateNextMonthlyRunAt(ScheduledAction scheduledAction, LocalDateTime currentRunAt) {
        LocalDateTime nextMonthCandidate = currentRunAt.plusMonths(scheduledAction.getIntervalValue());
        return buildMonthlyRunAt(
                currentRunAt,
                nextMonthCandidate.getYear(),
                nextMonthCandidate.getMonthValue(),
                scheduledAction.getDayOfMonth()
        );
    }

    private LocalDateTime buildMonthlyRunAt(LocalDateTime anchor, int year, int month, int dayOfMonth) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int resolvedDay = Math.min(dayOfMonth, yearMonth.lengthOfMonth());
        return yearMonth.atDay(resolvedDay).atTime(anchor.toLocalTime());
    }

    private Set<DayOfWeek> parseDaysOfWeek(List<String> requestDaysOfWeek, DayOfWeek defaultDayOfWeek) {
        Set<DayOfWeek> daysOfWeek = new LinkedHashSet<>();

        if (requestDaysOfWeek == null || requestDaysOfWeek.isEmpty()) {
            daysOfWeek.add(defaultDayOfWeek);
            return daysOfWeek;
        }

        for (String day : requestDaysOfWeek) {
            if (day == null || day.isBlank()) {
                continue;
            }
            try {
                daysOfWeek.add(DayOfWeek.valueOf(day.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid day of week: " + day);
            }
        }

        if (daysOfWeek.isEmpty()) {
            throw new IllegalArgumentException("At least one day of week is required for weekly schedules");
        }

        return daysOfWeek;
    }

    private List<String> splitDaysOfWeek(String daysOfWeekCsv) {
        if (daysOfWeekCsv == null || daysOfWeekCsv.isBlank()) {
            return List.of();
        }

        return List.of(daysOfWeekCsv.split(","));
    }

    private List<DayOfWeek> sortDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        return daysOfWeek.stream()
                .sorted(Comparator.comparingInt(DayOfWeek::getValue))
                .toList();
    }

    private String toDaysOfWeekCsv(Set<DayOfWeek> daysOfWeek) {
        return sortDaysOfWeek(daysOfWeek).stream()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(","));
    }

    private String normalizeRecurrenceType(String recurrenceType) {
        if (recurrenceType == null || recurrenceType.isBlank()) {
            throw new IllegalArgumentException("Recurrence type is required");
        }

        String normalizedValue = recurrenceType.trim().toUpperCase();
        if (Constant.SCHEDULE_FREQUENCY_DAILY.equals(normalizedValue)
                || Constant.SCHEDULE_FREQUENCY_WEEKLY.equals(normalizedValue)
                || Constant.SCHEDULE_FREQUENCY_MONTHLY.equals(normalizedValue)) {
            return normalizedValue;
        }

        throw new IllegalArgumentException("Recurrence type must be DAILY, WEEKLY or MONTHLY");
    }

    private String truncateMessage(String message) {
        if (message == null || message.isBlank()) {
            return "Unknown error";
        }

        return message.length() > 500 ? message.substring(0, 500) : message;
    }

    private void validateSavingGoalOwner(SavingGoal savingGoal, User loginUser) {
        if (!Objects.equals(savingGoal.getCreatedBy(), loginUser.getUsername())) {
            throw new ResourceNotFoundException("Saving goal not found by ID = " + savingGoal.getId());
        }
    }

    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }

    private <T> T runAsUser(User user, Supplier<T> action) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getRoleNames().stream()
                                    .map(roleName -> new SimpleGrantedAuthority(Objects.requireNonNullElse(roleName, Constant.ROLE_USER)))
                                    .toList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return action.get();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
        }
    }

    private ScheduledActionResponse toResponse(ScheduledAction scheduledAction) {
        return ScheduledActionResponse.builder()
                .id(scheduledAction.getId())
                .userId(scheduledAction.getUserId())
                .actionType(scheduledAction.getActionType())
                .scheduleState(scheduledAction.getScheduleState())
                .recurrenceType(scheduledAction.getRecurrenceType())
                .intervalValue(scheduledAction.getIntervalValue())
                .startAt(scheduledAction.getStartAt())
                .nextRunAt(scheduledAction.getNextRunAt())
                .lastRunAt(scheduledAction.getLastRunAt())
                .endAt(scheduledAction.getEndAt())
                .daysOfWeek(scheduledAction.getDaysOfWeek())
                .dayOfMonth(scheduledAction.getDayOfMonth())
                .lastExecutionStatus(scheduledAction.getLastExecutionStatus())
                .amount(scheduledAction.getAmount())
                .transactionType(scheduledAction.getTransactionType())
                .description(scheduledAction.getDescription())
                .categoryId(scheduledAction.getCategoryId())
                .savingGoalId(scheduledAction.getSavingGoalId())
                .failureReason(scheduledAction.getFailureReason())
                .retryCount(scheduledAction.getRetryCount())
                .build();
    }

    private record ScheduleSetup(
            String recurrenceType,
            Integer intervalValue,
            String daysOfWeekCsv,
            Integer dayOfMonth,
            LocalDateTime firstRunAt
    ) {
    }
}
