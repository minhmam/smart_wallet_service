package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.model.Category;
import com.minhpt.smart_wallet_service.model.User;
import org.springframework.data.domain.Page;

import java.util.List;


public interface TransactionService {

    TransactionResponse create(TransactionCreateRequest req);

    TransactionResponse update(TransactionCreateRequest req, Long id);

    TransactionResponse getDetail(Long id);

    Page<TransactionResponse> search(TransactionSearchRequest req);

    void delete(Long id);
}
