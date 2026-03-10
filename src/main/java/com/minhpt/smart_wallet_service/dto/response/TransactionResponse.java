package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Double amount;
    private String type;
    private String description;
    private Long categoryId;
    private Long userId;
    private LocalDateTime transactionDate;
    private Boolean aiPredicted;
}
