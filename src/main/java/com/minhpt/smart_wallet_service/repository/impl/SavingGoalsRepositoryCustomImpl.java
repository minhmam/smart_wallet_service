package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.SavingGoalsSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SavingGoalsResponse;
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

import java.math.BigDecimal;
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
                " sg.id , " +
                " sg.name , " +
                " sg.target_amount , " +
                " sg.current_amount , " +
                " sg.deadline, " +
                " sg.icon, " +
                " sg.color " +
                " from saving_goals sg " +
                " where sg.status = 1 " +
                " and (UPPER(sg.created_by)) = UPPER(:username)");

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            sql.append(" and (" +
                    "   upper(sg.created_by) like upper(:keySearch) " +
                    "   or upper(sg.name) like upper(:keySearch) " +
                    " )");
        }

        String sqlCount = "SELECT COUNT(*) FROM (" + sql.toString() + ") as Total";

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery(sqlCount);

        String username = authenticationUtil.getCurrentUser().getUsername();

        query.setParameter("username", username);
        queryCount.setParameter("username", username);

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            query.setParameter("keySearch", "%" + req.getKeySearch() + "%");
            queryCount.setParameter("keySearch", "%" + req.getKeySearch() + "%");
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();

        for (Object[] item : result) {
            SavingGoalsResponse response = SavingGoalsResponse.builder()
                    .id(item[0] != null ? (Long) item[0] : null)
                    .name(item[1] != null ? item[1].toString() : null)
                    .targetAmount(item[2] != null ? new BigDecimal(item[2].toString()) : null)
                    .currentAmount(item[3] != null ? new BigDecimal(item[3].toString()) : null)
                    .deadline(item[4] != null ? DataUtil.parseToLocalDateTime(item[3]) : null)
                    .icon(item[5] != null ? item[5].toString() : null)
                    .color(item[6] != null ? item[6].toString() : null)
                    .build();

            savingGoalsResponseList.add(response);
        }

        return new PageImpl<>(
                savingGoalsResponseList,
                PageRequest.of(page, size),
                (long) queryCount.getSingleResult()
        );
    }
}
