package com.minhpt.smart_wallet_service.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavingGoalsSearchRequest extends BaseRequest{
    private String name;
    private Double targetAmount;
    private Double currentAmount;
    private LocalDateTime deadline;
}
