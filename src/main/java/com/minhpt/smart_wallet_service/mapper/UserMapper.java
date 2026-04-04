package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.LinkedHashSet;
import java.util.Set;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {
    User toEntity(UserCreateRequest request);

    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    UserResponse toResponse(User user);

    default Set<String> mapRoles(User user) {
        if (user == null) {
            return new LinkedHashSet<>();
        }

        return user.getRoleNames();
    }
}
