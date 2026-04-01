package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.AccountBalanceDTO;
import com.minhpt.smart_wallet_service.model.AccountBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface AccountBalanceMapper {

    AccountBalanceDTO toDTO(AccountBalance entity);
}
