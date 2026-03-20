package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.mapper.SavingGoalsMapper;
import com.minhpt.smart_wallet_service.model.SavingGoal;
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

        SavingGoal savingGoal = savingGoalsMapper.toEntity(req);

        savingGoalsRepository.save(savingGoal);

        return savingGoalsMapper.toResponse(savingGoal);
    }

    @Override
    public SavingGoalsResponse update(SavingGoalsCreateRequest req, Long id) {

        SavingGoal updateSavingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Saving Goals not found by ID = " + id));

        savingGoalsMapper.update(updateSavingGoal, req);

        savingGoalsRepository.save(updateSavingGoal);

        return savingGoalsMapper.toResponse(updateSavingGoal);
    }

    @Override
    public List<SavingGoalsResponse> getAll() {

        String username = authenticationUtil.getCurrentUser().getUsername();

        List<SavingGoal> savingGoalList = savingGoalsRepository.findAllByCreatedByAndStatus(username, Constant.NOT_DELETE);

        return  savingGoalsMapper.toListResponse(savingGoalList);
    }

    @Override
    public SavingGoalsResponse getDetails(Long id) {

        SavingGoal savingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Saving Goals not found by ID = " + id));

        return savingGoalsMapper.toResponse(savingGoal);
    }

    @Override
    public void delete(Long id) {
        SavingGoal deleteSavingGoal = savingGoalsRepository.findByIdAndStatus(id, Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Saving Goals not found by ID = " + id));
        deleteSavingGoal.setStatus(Constant.DELETED);
        savingGoalsRepository.save(deleteSavingGoal);
    }

    @Override
    public Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req) {
        return  savingGoalsRepository.search(req);
    }
}
