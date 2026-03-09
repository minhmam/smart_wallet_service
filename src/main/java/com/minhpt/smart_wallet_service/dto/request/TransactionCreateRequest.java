package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionCreateRequest {
    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Type is required")
    @Pattern(
            regexp = "INCOME|EXPENSE",
            message = "Type must be either INCOME or EXPENSE"
    )
    private String type;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transantion date cannot be in the future")
    private LocalDateTime transactionDate;

    private Boolean aiPredicted;
}
