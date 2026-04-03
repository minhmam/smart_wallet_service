package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.CategorySearchRequest;
import com.minhpt.smart_wallet_service.dto.response.CategoryResponse;
import com.minhpt.smart_wallet_service.repository.CategoryRepositoryCustom;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    private final AuthenticationUtil authenticationUtil;
    @PersistenceContext
    private EntityManager em;

    public CategoryRepositoryCustomImpl(AuthenticationUtil authenticationUtil) {
        this.authenticationUtil = authenticationUtil;
    }

    @Override
    public Page<CategoryResponse> search(CategorySearchRequest request) {

        String username = authenticationUtil.getCurrentUser().getUsername();
        List<CategoryResponse> categories = new ArrayList<>();
        int page = request.getPage();
        int size = request.getSize();

        StringBuilder sql = new StringBuilder("""
                SELECT 
                    id, 
                    name, 
                    type, 
                    icon, 
                    color,
                    created_by
                FROM categories
                WHERE status = 1 
                """);
        if (!ObjectUtils.isEmpty(request.getName())) {
            sql.append(" AND name = :name ");
        }

        if (!ObjectUtils.isEmpty(request.getType())) {
            sql.append(" AND type LIKE :type");
        }

        sql.append(" AND (created_by = :loginName OR created_by = 'system') ");

        sql.append(" ORDER BY pin DESC, updated_at DESC");

        String countSQL = "SELECT COUNT(*) FROM ( " + sql + " ) as total";

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery(countSQL);

        query.setParameter("loginName", username);
        queryCount.setParameter("loginName", username);

        if (!ObjectUtils.isEmpty(request.getName())) {
            query.setParameter("name", "%" + request.getName() + "%");
            queryCount.setParameter("name", "%" + request.getName() + "%");
        }

        if (!ObjectUtils.isEmpty(request.getType())) {
            query.setParameter("type", request.getType());
            queryCount.setParameter("type", request.getType());
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();

        if (result != null && !result.isEmpty()) {
            for (Object[] item : result) {
                CategoryResponse response = new CategoryResponse();
                response.setId(item[0] != null ? (Long) item[0] : null);
                response.setName(item[1] != null ? item[1].toString() : null);
                response.setType(item[2] != null ? item[2].toString() : null);
                response.setIcon(item[3] != null ? item[3].toString() : null);
                response.setColor(item[4] != null ? item[4].toString() : null);
                response.setCreatedBy(item[5] != null ? item[5].toString() : null);
                categories.add(response);
            }
        }

        return new PageImpl<>(
                categories,
                PageRequest.of(page, size),
                (long) queryCount.getSingleResult()
        );
    }

    @Override
    public List<CategoryResponse> getTop5MostUsedCategories() {
        List<CategoryResponse> categories = new ArrayList<>();

        String sql = "SELECT " +
                " c.name, " +
                " c.type, " +
                " c.icon, " +
                " c.color, " +
                " c.created_by, " +
                " COUNT(t.id) AS used " +
                " FROM categories c " +
                " LEFT JOIN transactions t ON c.id = t.category_id " +
                " LEFT JOIN users u on u.id = t.user_id  " +
                " WHERE t.status = 1 and t.user_id  = :userId " +
                " GROUP BY c.id, c.name, c.type, c.icon " +
                " ORDER BY used DESC " +
                " LIMIT 5 ";

        Query query = em.createNativeQuery(sql);

        Long id = authenticationUtil.getCurrentUser().getId();

        query.setParameter("userId", id);

        List<Object[]> result = query.getResultList();

        if (result != null && !result.isEmpty()) {
            for (Object[] item : result) {
                CategoryResponse category = new CategoryResponse();

                category.setName(item[0] != null ? item[0].toString() : null);
                category.setType(item[1] != null ? item[1].toString() : null);
                category.setIcon(item[2] != null ? item[2].toString() : null);
                category.setColor(item[3] != null ? item[3].toString() : null);
                category.setCreatedBy(item[4] != null ? item[4].toString() : null);
                categories.add(category);
            }
        }
        return categories;
    }
}
