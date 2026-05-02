package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.UserSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.repository.UserRepositoryCustom;
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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<UserResponse> search(UserSearchRequest req) {
        List<UserResponse> userList = new ArrayList<>();
        int page = req.getPage();
        int size = req.getSize();

        StringBuilder sql = new StringBuilder("select " +
                " u.id, " +
                " u.username, " +
                " u.email, " +
                " u.full_name, " +
                " u.phone_number, " +
                " u.created_at, " +
                " string_agg(r.name, ',' order by r.name) as roles, " +
                " u.status " +
                " from users u " +
                " left join user_roles ur on ur.user_id = u.id " +
                " left join roles r on r.id = ur.role_id " +
                " where 1=1 ");

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            sql.append(" and (" +
                    " upper(u.full_name) like upper(:keySearch) " +
                    " or upper(u.username) like upper(:keySearch) " +
                    " or upper(u.email) like upper(:keySearch) " +
                    " ) ");
        }

        if (!ObjectUtils.isEmpty(req.getName())) {
            sql.append(" and (" +
                    " upper(u.full_name) like upper(:name) " +
                    " or upper(u.username) like upper(:name) " +
                    " ) ");
        }

        if (!ObjectUtils.isEmpty(req.getEmail())) {
            sql.append(" and upper(u.email) like upper(:email) ");
        }

        sql.append(" group by " +
                " u.id, " +
                " u.username, " +
                " u.email, " +
                " u.full_name, " +
                " u.phone_number, " +
                " u.created_at, " +
                " u.updated_at ");

        sql.append(" order by u.updated_at desc ");

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery("SELECT COUNT(*) FROM (" + sql + ") as total");

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            query.setParameter("keySearch", "%" + req.getKeySearch() + "%");
            queryCount.setParameter("keySearch", "%" + req.getKeySearch() + "%");
        }

        if (!ObjectUtils.isEmpty(req.getName())) {
            query.setParameter("name", "%" + req.getName() + "%");
            queryCount.setParameter("name", "%" + req.getName() + "%");
        }

        if (!ObjectUtils.isEmpty(req.getEmail())) {
            query.setParameter("email", "%" + req.getEmail() + "%");
            queryCount.setParameter("email", "%" + req.getEmail() + "%");
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();

        for (Object[] item : result) {
            UserResponse response = UserResponse.builder()
                    .id(item[0] != null ? ((Number) item[0]).longValue() : null)
                    .username(item[1] != null ? item[1].toString() : null)
                    .email(item[2] != null ? item[2].toString() : null)
                    .fullName(item[3] != null ? item[3].toString() : null)
                    .phoneNumber(item[4] != null ? item[4].toString() : null)
                    .createdAt(DataUtil.parseToLocalDateTime(item[5]))
                    .roles(parseRoles(item[6]))
                    .status(item[7] != null ? ((Number) item[7]).intValue() : null)
                    .build();

            userList.add(response);
        }

        Number total = (Number) queryCount.getSingleResult();

        return new PageImpl<>(
                userList,
                PageRequest.of(page, size),
                total.longValue()
        );
    }

    private Set<String> parseRoles(Object value) {
        Set<String> roles = new LinkedHashSet<>();

        if (value == null || value.toString().isBlank()) {
            return roles;
        }

        Arrays.stream(value.toString().split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .forEach(roles::add);

        return roles;
    }
}
