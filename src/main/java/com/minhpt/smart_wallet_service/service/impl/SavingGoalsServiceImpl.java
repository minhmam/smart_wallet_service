package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.mapper.SavingGoalsMapper;
import com.minhpt.smart_wallet_service.model.SavingGoals;
import com.minhpt.smart_wallet_service.repository.SavingGoalsRepository;
import com.minhpt.smart_wallet_service.service.SavingGoalsService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingGoalsServiceImpl implements SavingGoalsService {

    private final SavingGoalsRepository savingGoalsRepository;
    private final SavingGoalsMapper savingGoalsMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public SavingGoalsResponse create(SavingGoalsCreateRequest req) {

        SavingGoals savingGoals = savingGoalsMapper.toEntity(req);

        savingGoalsRepository.save(savingGoals);

        return savingGoalsMapper.toResponse(savingGoals);
    }

    @Override
    public SavingGoalsResponse update(SavingGoalsCreateRequest req, Long id) {

        SavingGoals updateSavingGoals = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Saving Goals not found by ID = " + id));

        savingGoalsMapper.update(updateSavingGoals, req);

        savingGoalsRepository.save(updateSavingGoals);

        return savingGoalsMapper.toResponse(updateSavingGoals);
    }

    @Override
    public List<SavingGoalsResponse> getAll() {

        String username = authenticationUtil.getCurrentUser().getUsername();

        List<SavingGoals> savingGoalsList = savingGoalsRepository.findAllByCreatedByAndStatus(username, Constant.NOT_DELETE);

        return  savingGoalsMapper.toListResponse(savingGoalsList);
    }

    @Override
    public SavingGoalsResponse getDetails(Long id) {

        SavingGoals savingGoals = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Saving Goals not found by ID = " + id));

        return savingGoalsMapper.toResponse(savingGoals);
    }

    @Override
    public void delete(Long id) {
        SavingGoals deleteSavingGoals = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Saving Goals not found by ID = " + id));
        deleteSavingGoals.setStatus(Constant.DELETED);
        savingGoalsRepository.save(deleteSavingGoals);
    }

    @Override
    public Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req) {
        return  savingGoalsRepository.search(req);
    }
}
