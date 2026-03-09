package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.mapper.TransactionMapper;
import com.minhpt.smart_wallet_service.model.Transaction;
import com.minhpt.smart_wallet_service.repository.TransactionRepository;
import com.minhpt.smart_wallet_service.service.TransactionService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public TransactionResponse create(TransactionCreateRequest req) {
        Transaction transaction = transactionMapper.toEntity(req);

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public TransactionResponse update(TransactionCreateRequest req, Long id) {
        Transaction updateTransaction = transactionRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Not transaction found by transaction id = " + id));

        if(req.getAmount() != null){
            updateTransaction.setAmount(req.getAmount());
        }

        if(req.getType() != null){
            updateTransaction.setType(req.getType());
        }

        if(req.getDescription() != null){
            updateTransaction.setDescription(req.getDescription());
        }

        if(req.getCategoryId() != null){
            updateTransaction.setCategoryId(req.getCategoryId());
        }

        if(req.getTransactionDate() != null){
            updateTransaction.setTransactionDate(req.getTransactionDate());
        }

        if(req.getAiPredicted() != null){
            updateTransaction.setAiPredicted(req.getAiPredicted());
        }

        return transactionMapper.toResponse(transactionRepository.save(updateTransaction));
    }

    @Override
    public List<TransactionResponse> getAll() {

        Long userID = authenticationUtil.getCurrentUser().getId();
        List<Transaction> transactionList = transactionRepository.findAllByUserId(userID);

        return transactionMapper.toListResponse(transactionList);
    }

    @Override
    public TransactionResponse getDetailsTransaction(Long id) {
        Transaction detailTransaction = transactionRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Not transaction found by id + " + id));

        return transactionMapper.toResponse(detailTransaction);
    }

    @Override
    public Page<TransactionResponse> search(TransactionSearchRequest req) {
        return transactionRepository.search(req);
    }

    @Override
    public void delete(Long id) {
        Transaction deleteTransaction = transactionRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Transaction not found by ID = " + id));

        deleteTransaction.setStatus(Constant.DELETED);
        transactionRepository.save(deleteTransaction);
    }
}
