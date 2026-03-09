package com.minhpt.smart_wallet_service.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionSearchRequest extends BaseRequest {
    private Double amount;
    private String type;
    private String description;
    private LocalDateTime transactionDate;
    private Boolean aiPredicted;
}
