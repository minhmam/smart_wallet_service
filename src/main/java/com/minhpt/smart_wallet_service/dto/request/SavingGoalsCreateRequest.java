package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalsCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Target Amount is required")
    @Positive(message = "Target Amount must be > 0")
    private Double targetAmount;

    @NotNull(message = "currentAmount Amount is required")
    @Positive(message = "currentAmount Amount must be > 0")
    private Double currentAmount;

    @NotNull(message = "Deadline is required")
    @FutureOrPresent(message = "Deadline is in the present or future")
    private LocalDateTime deadline;
}
