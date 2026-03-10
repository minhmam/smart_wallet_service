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
public class TransactionSearchRequest extends BaseRequest {
    private Double amount;
    private String type;
    private String description;
    private LocalDateTime transactionDate;
    private Boolean aiPredicted;
}
