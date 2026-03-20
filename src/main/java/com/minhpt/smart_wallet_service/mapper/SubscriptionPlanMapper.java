package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.model.SubscriptionPlan;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface SubscriptionPlanMapper {

    SubscriptionPlanResponse toResponse(SubscriptionPlan subscriptionPlan);
}
