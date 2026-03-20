package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.model.SavingGoal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface SavingGoalsMapper {

    SavingGoal toEntity(SavingGoalsCreateRequest req);

    SavingGoalsResponse toResponse(SavingGoal savingGoal);

    List<SavingGoalsResponse> toListResponse(List<SavingGoal> savingGoalList);

    void update(
            @MappingTarget SavingGoal savingGoal,
            SavingGoalsCreateRequest req
    );
}
