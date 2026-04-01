package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.mapper.TransactionMapper;
import com.minhpt.smart_wallet_service.model.AccountBalance;
import com.minhpt.smart_wallet_service.model.Category;
import com.minhpt.smart_wallet_service.model.Transaction;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
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
    private final AccountBalanceRepository accountBalanceRepository;

    @Override
    public TransactionResponse create(TransactionCreateRequest req) {

        //Lấy user
        User loginUser = authenticationUtil.getCurrentUser();

        if(loginUser == null){
            throw new RuntimeException("Chưa có user nào login cả");
        }

        //Cập nhật Transaciton
        Transaction transaction = transactionMapper.toEntity(req);
        transaction.setCreatedBy(loginUser.getUsername());
        transaction.setUpdatedBy(loginUser.getUsername());
        transaction.setUserId(loginUser.getId());

        //Cập nhật account balance
        AccountBalance accountBalance = accountBalanceRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không có thanh khoản"));

        if(transaction.getType().equals("EXPENSE")){
            accountBalance.setBalance(accountBalance.getBalance().subtract(transaction.getAmount()));
            accountBalanceRepository.save(accountBalance);
        }

        if(transaction.getType().equals("INCOME")){
            accountBalance.setBalance(accountBalance.getBalance().add(transaction.getAmount()));
            accountBalanceRepository.save(accountBalance);
        }

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

    @Override
    public List<TransactionResponse> findByType(String type) {
        User loginUser = authenticationUtil.getCurrentUser();
        if(loginUser == null){
            throw new RuntimeException("Chưa có user đăng nhập vào hệ thống");
        }
        List<Transaction> transactionList = transactionRepository.findByTypeAndUserIdAndStatus(type, loginUser.getId(), Constant.NOT_DELETE);

        return transactionMapper.toListResponse(transactionList);
    }
}
