package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;

import java.util.List;


public interface TransactionService {
    TransactionResponse create(TransactionCreateRequest req);
    TransactionResponse update(TransactionCreateRequest req, Long id);

    List<TransactionResponse> getAll();

    TransactionResponse getDetailsTransaction(Long id);

    Page<TransactionResponse> search(TransactionSearchRequest req);

    void delete(Long id);
}
