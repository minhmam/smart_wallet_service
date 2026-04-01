package com.minhpt.smart_wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrTransactionResponse {
    private Long categoryId;
    private String description;
    private Long amount;
    private String transactionDate;
    private String type;
}
