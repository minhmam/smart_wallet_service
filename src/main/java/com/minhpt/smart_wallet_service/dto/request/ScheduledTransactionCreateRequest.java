package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransactionCreateRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Type must be either INCOME or EXPENSE")
    private String type;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
    private LocalDateTime startAt;

    @NotBlank(message = "Recurrence type is required")
    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY", message = "Recurrence type must be DAILY, WEEKLY or MONTHLY")
    private String recurrenceType;

    @Min(value = 1, message = "Interval value must be greater than or equal to 1")
    private Integer intervalValue;

    private List<String> daysOfWeek;

    private Integer dayOfMonth;

    private LocalDateTime endAt;
}
