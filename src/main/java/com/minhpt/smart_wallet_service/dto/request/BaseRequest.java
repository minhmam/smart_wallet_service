package com.minhpt.smart_wallet_service.dto.request;

import lombok.Data;

@Data
public class BaseRequest {
    private String keySearch;
    private Integer page;
    private Integer size;
}
