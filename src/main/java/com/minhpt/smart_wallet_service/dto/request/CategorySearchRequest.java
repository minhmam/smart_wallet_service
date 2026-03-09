package com.minhpt.smart_wallet_service.dto.request;

import lombok.Data;

@Data
public class CategorySearchRequest extends BaseRequest{
    private String name;
    private String type;
}
