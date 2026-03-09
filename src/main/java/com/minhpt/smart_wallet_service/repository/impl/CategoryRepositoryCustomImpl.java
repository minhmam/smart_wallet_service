package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.repository.CategoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<CategoryResponse> search(CategorySearchRequest request) {
        List<CategoryResponse> categories = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT name, type, icon
                FROM categories
                WHERE status = 1
                """);
        if (request.getName() != null) {
            sql.append(" AND name LIKE :name ");
        }

        if (request.getType() != null) {
            sql.append(" AND type LIKE :type");
        }

        sql.append(" ORDER BY pin DESC, updated_at DESC");

        String countSQL = "SELECT COUNT(*) FROM ( " + sql + " ) as toal";

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery(countSQL);

        if (request.getName() != null && !request.getName().isBlank()) {
            query.setParameter("name", "%" + request.getName() + "%");
            queryCount.setParameter("name", "%" + request.getName() + "%");
        }

        if (request.getType() != null && !request.getType().isBlank()) {
            query.setParameter("type", request.getType());
            queryCount.setParameter("type", request.getType());
        }

        int page = request.getPage();
        int size = request.getSize();

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();
        Long total = ((Number) queryCount.getSingleResult()).longValue();

        if (result != null && !result.isEmpty()) {
            for (Object[] item : result) {
                CategoryResponse response = new CategoryResponse();

                response.setName(item[0] != null ? item[0].toString() : null);
                response.setType(item[1] != null ? item[1].toString() : null);
                response.setIcon(item[2] != null ? item[2].toString() : null);

                categories.add(response);
            }
        }

        return new PageImpl<>(
                categories,
                PageRequest.of(page, size),
                total
        );
    }
}
