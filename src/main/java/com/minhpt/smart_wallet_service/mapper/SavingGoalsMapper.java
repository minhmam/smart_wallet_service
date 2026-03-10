package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.model.SavingGoals;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface SavingGoalsMapper {

    SavingGoals toEntity(SavingGoalsCreateRequest req);

    SavingGoalsResponse toResponse(SavingGoals savingGoals);

    List<SavingGoalsResponse> toListResponse(List<SavingGoals> savingGoalsList);

    void update(
            @MappingTarget SavingGoals savingGoals,
            SavingGoalsCreateRequest req
    );
}
