package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.SubscriptionPlanSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.SubscriptionPlanResponse;
import com.minhpt.smart_wallet_service.repository.SubscriptionPlanRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SubscriptionPlanRepositoryCustomImpl implements SubscriptionPlanRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<SubscriptionPlanResponse> search(SubscriptionPlanSearchRequest request) {
        SubscriptionPlanSearchRequest req = request == null ? new SubscriptionPlanSearchRequest() : request;
        List<SubscriptionPlanResponse> plans = new ArrayList<>();
        int page = req.getPage() == null ? 0 : req.getPage();
        int size = req.getSize() == null ? 10 : req.getSize();

        StringBuilder sql = new StringBuilder("""
                select
                    sp.id,
                    sp.code,
                    sp.name,
                    sp.price,
                    sp.duration_days,
                    sp.status
                from subscription_plans sp
                where sp.status = 1
                """);

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            sql.append("""
                     and (
                        upper(sp.code) like upper(:keySearch)
                        or upper(sp.name) like upper(:keySearch)
                        or upper(sp.description) like upper(:keySearch)
                     )
                    """);
        }

        if (!ObjectUtils.isEmpty(req.getCode())) {
            sql.append(" and upper(sp.code) like upper(:code) ");
        }

        if (!ObjectUtils.isEmpty(req.getName())) {
            sql.append(" and upper(sp.name) like upper(:name) ");
        }

        if (req.getMinPrice() != null) {
            sql.append(" and sp.price >= :minPrice ");
        }

        if (req.getMaxPrice() != null) {
            sql.append(" and sp.price <= :maxPrice ");
        }

        sql.append(" order by sp.updated_at desc ");

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery("select count(*) from (" + sql + ") as total");

        setParameters(query, req);
        setParameters(queryCount, req);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();
        for (Object[] item : result) {
            plans.add(SubscriptionPlanResponse.builder()
                    .id(item[0] != null ? ((Number) item[0]).longValue() : null)
                    .code(item[1] != null ? item[1].toString() : null)
                    .name(item[2] != null ? item[2].toString() : null)
                    .price(item[3] != null ? ((Number) item[3]).longValue() : null)
                    .durationDays(item[4] != null ? ((Number) item[4]).longValue() : null)
                    .status(item[5] != null ? ((Number) item[5]).intValue() : null)
                    .build());
        }

        Number total = (Number) queryCount.getSingleResult();
        return new PageImpl<>(
                plans,
                PageRequest.of(page, size),
                total.longValue()
        );
    }

    private void setParameters(Query query, SubscriptionPlanSearchRequest req) {
        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            query.setParameter("keySearch", "%" + req.getKeySearch() + "%");
        }

        if (!ObjectUtils.isEmpty(req.getCode())) {
            query.setParameter("code", "%" + req.getCode() + "%");
        }

        if (!ObjectUtils.isEmpty(req.getName())) {
            query.setParameter("name", "%" + req.getName() + "%");
        }

        if (req.getMinPrice() != null) {
            query.setParameter("minPrice", req.getMinPrice());
        }

        if (req.getMaxPrice() != null) {
            query.setParameter("maxPrice", req.getMaxPrice());
        }
    }
}
