package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.TransactionCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.TransactionMapper;
import com.minhpt.smart_wallet_service.model.Transaction;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.TransactionRepository;
import com.minhpt.smart_wallet_service.service.AccountBalanceService;
import com.minhpt.smart_wallet_service.service.TransactionService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuthenticationUtil authenticationUtil;
    private final AccountBalanceService accountBalanceService;

    @Override
    @Transactional
    public TransactionResponse create(TransactionCreateRequest req) {
        User loginUser = authenticationUtil.getCurrentUser();

        Transaction transaction = transactionMapper.toEntity(req);
        transaction.setCreatedBy(loginUser.getUsername());
        transaction.setUpdatedBy(loginUser.getUsername());
        transaction.setUserId(loginUser.getId());

        Transaction savedTransaction = transactionRepository.save(transaction);
        adjustWalletBalance(calculateBalanceEffect(savedTransaction.getType(), savedTransaction.getAmount()));

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse update(TransactionCreateRequest req, Long id) {
        User loginUser = authenticationUtil.getCurrentUser();

        Transaction updateTransaction = transactionRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found by ID = " + id));
        validateTransactionOwner(updateTransaction, loginUser);

        BigDecimal currentEffect = calculateBalanceEffect(updateTransaction.getType(), updateTransaction.getAmount());

        updateTransaction.setAmount(req.getAmount());
        updateTransaction.setType(req.getType());
        updateTransaction.setDescription(req.getDescription());
        updateTransaction.setCategoryId(req.getCategoryId());
        updateTransaction.setTransactionDate(req.getTransactionDate());
        updateTransaction.setAiPredicted(req.getAiPredicted());
        updateTransaction.setUpdatedBy(loginUser.getUsername());

        BigDecimal newEffect = calculateBalanceEffect(updateTransaction.getType(), updateTransaction.getAmount());
        BigDecimal balanceDelta = newEffect.subtract(currentEffect);

        Transaction savedTransaction = transactionRepository.save(updateTransaction);
        adjustWalletBalance(balanceDelta);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public TransactionResponse getDetail(Long id) {
        User loginUser = authenticationUtil.getCurrentUser();

        Transaction detailTransaction = transactionRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found by ID = " + id));
        validateTransactionOwner(detailTransaction, loginUser);

        return transactionMapper.toResponse(detailTransaction);
    }

    @Override
    public Page<TransactionResponse> search(TransactionSearchRequest req) {
        return transactionRepository.search(req);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User loginUser = authenticationUtil.getCurrentUser();

        Transaction deleteTransaction = transactionRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found by ID = " + id));
        validateTransactionOwner(deleteTransaction, loginUser);

        BigDecimal balanceDelta = calculateBalanceEffect(deleteTransaction.getType(), deleteTransaction.getAmount()).negate();

        deleteTransaction.setStatus(Constant.DELETED);
        deleteTransaction.setUpdatedBy(loginUser.getUsername());

        transactionRepository.save(deleteTransaction);
        adjustWalletBalance(balanceDelta);
    }

    private void adjustWalletBalance(BigDecimal balanceDelta) {
        if (balanceDelta == null || balanceDelta.signum() == 0) {
            return;
        }

        if (balanceDelta.signum() > 0) {
            accountBalanceService.addBalance(balanceDelta);
            return;
        }

        accountBalanceService.subtractBalance(balanceDelta.abs());
    }

    private BigDecimal calculateBalanceEffect(String type, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount is invalid");
        }

        if ("INCOME".equals(type)) {
            return amount;
        }

        if ("EXPENSE".equals(type)) {
            return amount.negate();
        }

        throw new IllegalArgumentException("Invalid transaction type: " + type);
    }

    private void validateTransactionOwner(Transaction transaction, User loginUser) {
        if (!loginUser.getId().equals(transaction.getUserId())) {
            throw new ResourceNotFoundException("Transaction not found by ID = " + transaction.getId());
        }
    }
}
