package com.minhpt.smart_wallet_service.repository;

import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;

public interface TransactionRepositoryCustom {
    Page<TransactionResponse> search(TransactionSearchRequest req);
}
