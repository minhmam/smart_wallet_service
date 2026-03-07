package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    @Pattern(
            regexp = "INCOME|EXPENSE",
            message = "Type must be either INCOME or EXPENSE"
    )
    private String type;

    @NotBlank(message = "Icon is required")
    private String icon;
}
