package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.TransactionSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.TransactionResponse;
import com.minhpt.smart_wallet_service.repository.TransactionRepositoryCustom;
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
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {
    private final AuthenticationUtil authenticationUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<TransactionResponse> search(TransactionSearchRequest req) {
        List<TransactionResponse> transactionList = new ArrayList<>();
        int page = req.getPage();
        int size = req.getSize();

        StringBuilder sql = new StringBuilder("select " +
                " t.id, " +
                " t.amount, " +
                " t.type, " +
                " t.description , " +
                " t.category_id, " +
                " t.user_id , " +
                " t.transaction_date, " +
                " t.ai_predicted, " +
                " c.\"name\" category_name, " +
                " c.color color, " +
                " c.icon icon " +
                " from transactions t " +
                " left join categories c on t.category_id = c.id " +
                " where t.STATUS = 1 " +
                " and t.created_by = :username ");

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            sql.append(" and (upper(t.description) like upper(:keySearch)) ");
        }

        if (!ObjectUtils.isEmpty(req.getType())) {
            sql.append(" and t.TYPE = :type ");
        }

        sql.append(" ORDER BY t.updated_at DESC ");

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery("SELECT COUNT(*) FROM (" + sql + ") as total");

        query.setParameter("username", authenticationUtil.getCurrentUser().getUsername());
        queryCount.setParameter("username", authenticationUtil.getCurrentUser().getUsername());

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            query.setParameter("keySearch", "%" + req.getKeySearch() + "%");
            queryCount.setParameter("keySearch", "%" + req.getKeySearch() + "%");
        }

        if (!ObjectUtils.isEmpty(req.getType())) {
            query.setParameter("type", req.getType());
            queryCount.setParameter("type", req.getType());
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();

        for (Object[] item : result) {
            TransactionResponse response = TransactionResponse.builder()
                    .id(item[0] != null ? ((Number) item[0]).longValue() : null)
                    .amount(item[1] != null ? new BigDecimal(item[1].toString()) : null)
                    .type(item[2] != null ? item[2].toString() : null)
                    .description(item[3] != null ? item[3].toString() : null)
                    .categoryId(item[4] != null ? ((Number) item[4]).longValue() : null)
                    .userId(item[5] != null ? ((Number) item[5]).longValue() : null)
                    .transactionDate(DataUtil.parseToLocalDateTime(item[6]))
                    .aiPredicted(item[7] != null && Boolean.parseBoolean(item[7].toString()))
                    .categoryName(item[8] != null ? item[8].toString() : null)
                    .color(item[9] != null ? item[9].toString() : null)
                    .icon(item[10] != null ? item[10].toString() : null)
                    .build();

            transactionList.add(response);
        }

        return new PageImpl<>(
                transactionList,
                PageRequest.of(page, size),
                (long) queryCount.getSingleResult()
        );
    }
}
