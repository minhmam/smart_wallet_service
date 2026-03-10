package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
import com.minhpt.smart_wallet_service.model.SavingGoals;
import com.minhpt.smart_wallet_service.repository.SavingGoalsRepositoryCustom;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import com.minhpt.smart_wallet_service.util.DataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SavingGoalsRepositoryCustomImpl implements SavingGoalsRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private final AuthenticationUtil authenticationUtil;

    @Override
    public Page<SavingGoalsResponse> search(SavingGoalsSearchRequest req) {

        List<SavingGoalsResponse> savingGoalsResponseList = new ArrayList<>();
        int page = req.getPage();
        int size = req.getSize();

        StringBuilder sql = new StringBuilder("select " +
                "sg.name , " +
                "sg.target_amount , " +
                "sg.current_amount , " +
                "sg.deadline, " +
                "from " +
                "saving_goals sg " +
                "where " +
                "sg.status = 1 " +
                "and (UPPER(sg.created_by)) like UPPER(:name)");

        if(!ObjectUtils.isEmpty(req.getKeySearch())){
            sql.append("and ((upper(sg.created_by) like upper(:keySearch)) " +
                    "or (upper(sg.name) like upper(:keySearch)))");
        }

        String sqlCount = "SELECT COUNT(*) FROM (" + sql.toString() + ") as Total";

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery(sqlCount);

        String username = authenticationUtil.getCurrentUser().getUsername();

        if(!ObjectUtils.isEmpty(username))
        {
            query.setParameter("name", "%"+ username + "%");
            queryCount.setParameter("name", "%"+ username + "%");
        }

        if(!ObjectUtils.isEmpty(req.getKeySearch()))
        {
            query.setParameter("keySearch", "%"+ req.getKeySearch() + "%");
            queryCount.setParameter("keySearch", "%"+ req.getKeySearch() + "%");
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();

        for(Object[] item : result){

            SavingGoalsResponse response = SavingGoalsResponse.builder()
                    .name(item[0] != null ? item[0].toString() : null)
                    .targetAmount(item[1] != null ? ((Number) item[1]).doubleValue() : null)
                    .currentAmount(item[2] != null? ((Number) item[2]).doubleValue() : null)
                    .deadline(item[3] != null? DataUtil.parseToLocalDateTime(item[3]) : null)
                    .build();

            savingGoalsResponseList.add(response);
        };

        return new PageImpl<>(
                savingGoalsResponseList,
                PageRequest.of(page, size),
                (long) queryCount.getSingleResult()
        );
    }
}
