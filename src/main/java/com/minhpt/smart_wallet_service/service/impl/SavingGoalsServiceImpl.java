package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.SavingGoalsMapper;
import com.minhpt.smart_wallet_service.model.SavingGoal;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.SavingGoalsRepository;
import com.minhpt.smart_wallet_service.service.AccountBalanceService;
import com.minhpt.smart_wallet_service.service.SavingGoalsService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SavingGoalsServiceImpl implements SavingGoalsService {

    private final SavingGoalsRepository savingGoalsRepository;
    private final SavingGoalsMapper savingGoalsMapper;
    private final AuthenticationUtil authenticationUtil;
    private final AccountBalanceService accountBalanceService;

    @Override
    public SavingGoalsResponse create(SavingGoalsCreateRequest req) {
        User loginUser = getCurrentUser();

        SavingGoal savingGoal = savingGoalsMapper.toEntity(req);
        savingGoal.setCreatedBy(loginUser.getUsername());
        savingGoal.setUpdatedBy(loginUser.getUsername());

        savingGoalsRepository.save(savingGoal);

        return savingGoalsMapper.toResponse(savingGoal);
    }

    @Override
    public SavingGoalsResponse update(SavingGoalsCreateRequest req, Long id) {
        User loginUser = getCurrentUser();

        SavingGoal updateSavingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found by ID = " + id));

        savingGoalsMapper.update(updateSavingGoal, req);
        updateSavingGoal.setUpdatedBy(loginUser.getUsername());

        savingGoalsRepository.save(updateSavingGoal);

        return savingGoalsMapper.toResponse(updateSavingGoal);
    }

    @Override
    public SavingGoalsResponse getDetails(Long id) {
        SavingGoal savingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found by ID = " + id));

        return savingGoalsMapper.toResponse(savingGoal);
    }

    @Override
    public void delete(Long id) {
        String username = getCurrentUser().getUsername();

        SavingGoal deleteSavingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found by ID = " + id));

        deleteSavingGoal.setStatus(Constant.DELETED);
        deleteSavingGoal.setUpdatedBy(username);

        savingGoalsRepository.save(deleteSavingGoal);
    }

    @Override
    @Transactional
    public SavingGoalsResponse deposit(Long id, BigDecimal amount) {
        User loginUser = getCurrentUser();

        SavingGoal savingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found by ID = " + id));
        validateGoalOwner(savingGoal, loginUser);
        validateDepositAmount(savingGoal, amount);

        accountBalanceService.subtractBalance(amount);

        savingGoal.setCurrentAmount(getSafeAmount(savingGoal.getCurrentAmount()).add(amount));
        savingGoal.setUpdatedBy(loginUser.getUsername());

        SavingGoal savedSavingGoal = savingGoalsRepository.save(savingGoal);
        return savingGoalsMapper.toResponse(savedSavingGoal);
    }

    @Override
    public Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req) {
        return savingGoalsRepository.search(req);
    }

    private void validateGoalOwner(SavingGoal savingGoal, User loginUser) {
        if (!loginUser.getUsername().equals(savingGoal.getCreatedBy())) {
            throw new ResourceNotFoundException("Saving goal not found by ID = " + savingGoal.getId());
        }
    }

    private void validateDepositAmount(SavingGoal savingGoal, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        BigDecimal updatedAmount = getSafeAmount(savingGoal.getCurrentAmount()).add(amount);
        BigDecimal targetAmount = getSafeAmount(savingGoal.getTargetAmount());

        if (targetAmount.signum() > 0 && updatedAmount.compareTo(targetAmount) > 0) {
            throw new IllegalArgumentException("Deposit amount exceeds saving goal target");
        }
    }

    private BigDecimal getSafeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private User getCurrentUser() {
        User loginUser = authenticationUtil.getCurrentUser();
        if (loginUser == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }
        return loginUser;
    }
}
