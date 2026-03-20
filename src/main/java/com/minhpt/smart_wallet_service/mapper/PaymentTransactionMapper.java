package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.response.PaymentTransactionResponse;
import com.minhpt.smart_wallet_service.model.PaymentTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PaymentTransactionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "subscriptionPlan.id", target = "subscriptionPlanId")
    PaymentTransactionResponse toResponse(PaymentTransaction entity);

    List<PaymentTransactionResponse> toResponseList(List<PaymentTransaction> entities);
}
