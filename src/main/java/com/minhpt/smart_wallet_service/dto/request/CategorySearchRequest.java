package com.minhpt.smart_wallet_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchRequest extends BaseRequest {
    private String name;
    private String type;
}
