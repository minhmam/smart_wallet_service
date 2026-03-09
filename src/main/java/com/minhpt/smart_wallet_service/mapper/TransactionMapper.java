package com.minhpt.smart_wallet_service.mapper;

import com.minhpt.smart_wallet_service.config.MapStructConfig;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.model.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface TransactionMapper {
    Transaction toEntity(TransactionCreateRequest request);
    TransactionResponse toResponse(Transaction transaction);
    List<TransactionResponse> toListResponse(List<Transaction> transactionList);
}
