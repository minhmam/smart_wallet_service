package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingGoalsResponse {
    private Long id;
    private String name;
    private Double targetAmount;
    private Double currentAmount;
    private LocalDateTime deadline;
}
