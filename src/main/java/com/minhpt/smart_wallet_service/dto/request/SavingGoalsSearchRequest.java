package com.minhpt.smart_wallet_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalsSearchRequest extends BaseRequest {
    private String name;
    private Double targetAmount;
    private Double currentAmount;
    private LocalDateTime deadline;
}
