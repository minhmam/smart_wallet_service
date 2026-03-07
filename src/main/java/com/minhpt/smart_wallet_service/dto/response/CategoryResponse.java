package com.minhpt.smart_wallet_service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String type;
    private String icon;
    private LocalDateTime createdAt;
}
