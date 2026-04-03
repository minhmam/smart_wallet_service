package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);
}
