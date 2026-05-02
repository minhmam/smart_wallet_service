package com.minhpt.smart_wallet_service.repository.impl;

import com.minhpt.smart_wallet_service.dto.request.NotificationScheduleSearchRequest;
import com.minhpt.smart_wallet_service.dto.response.NotificationScheduleResponse;
import com.minhpt.smart_wallet_service.repository.NotificationScheduleRepositoryCustom;
import com.minhpt.smart_wallet_service.util.DataUtil;
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
public class NotificationScheduleRepositoryCustomImpl implements NotificationScheduleRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<NotificationScheduleResponse> search(NotificationScheduleSearchRequest req) {
        List<NotificationScheduleResponse> schedules = new ArrayList<>();
        int page = req.getPage() == null ? 0 : req.getPage();
        int size = req.getSize() == null ? 10 : req.getSize();

        StringBuilder sql = new StringBuilder("""
                select
                    ns.id,
                    ns.title,
                    ns.content,
                    ns.target_group,
                    ns.schedule_state,
                    ns.recurrence_type,
                    ns.start_at,
                    ns.next_run_at,
                    ns.last_run_at,
                    ns.last_execution_status,
                    ns.last_target_user_count,
                    ns.last_success_count,
                    ns.last_failure_count,
                    ns.failure_reason,
                    ns.retry_count,
                    ns.created_at
                from notification_schedules ns
                where ns.status = 1
                """);

        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            sql.append(" and (upper(ns.title) like upper(:keySearch) or upper(ns.content) like upper(:keySearch)) ");
        }

        if (!ObjectUtils.isEmpty(req.getTargetGroup())) {
            sql.append(" and upper(ns.target_group) = upper(:targetGroup) ");
        }

        if (!ObjectUtils.isEmpty(req.getScheduleState())) {
            sql.append(" and upper(ns.schedule_state) = upper(:scheduleState) ");
        }

        if (!ObjectUtils.isEmpty(req.getRecurrenceType())) {
            sql.append(" and upper(ns.recurrence_type) = upper(:recurrenceType) ");
        }

        if (req.getFromDate() != null) {
            sql.append(" and ns.start_at >= :fromDate ");
        }

        if (req.getToDate() != null) {
            sql.append(" and ns.start_at <= :toDate ");
        }

        sql.append(" order by ns.updated_at desc ");

        Query query = em.createNativeQuery(sql.toString());
        Query queryCount = em.createNativeQuery("select count(*) from (" + sql + ") as total");

        setSearchParameters(query, req);
        setSearchParameters(queryCount, req);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Object[]> result = query.getResultList();
        for (Object[] item : result) {
            schedules.add(NotificationScheduleResponse.builder()
                    .id(item[0] != null ? ((Number) item[0]).longValue() : null)
                    .title(item[1] != null ? item[1].toString() : null)
                    .content(item[2] != null ? item[2].toString() : null)
                    .targetGroup(item[3] != null ? item[3].toString() : null)
                    .scheduleState(item[4] != null ? item[4].toString() : null)
                    .recurrenceType(item[5] != null ? item[5].toString() : null)
                    .startAt(DataUtil.parseToLocalDateTime(item[6]))
                    .nextRunAt(DataUtil.parseToLocalDateTime(item[7]))
                    .lastRunAt(DataUtil.parseToLocalDateTime(item[8]))
                    .lastExecutionStatus(item[9] != null ? item[9].toString() : null)
                    .lastTargetUserCount(toLong(item[10]))
                    .lastSuccessCount(toLong(item[11]))
                    .lastFailureCount(toLong(item[12]))
                    .failureReason(item[13] != null ? item[13].toString() : null)
                    .retryCount(item[14] != null ? ((Number) item[14]).intValue() : null)
                    .createdAt(DataUtil.parseToLocalDateTime(item[15]))
                    .build());
        }

        Number total = (Number) queryCount.getSingleResult();
        return new PageImpl<>(
                schedules,
                PageRequest.of(page, size),
                total.longValue()
        );
    }

    private void setSearchParameters(Query query, NotificationScheduleSearchRequest req) {
        if (!ObjectUtils.isEmpty(req.getKeySearch())) {
            query.setParameter("keySearch", "%" + req.getKeySearch() + "%");
        }

        if (!ObjectUtils.isEmpty(req.getTargetGroup())) {
            query.setParameter("targetGroup", req.getTargetGroup());
        }

        if (!ObjectUtils.isEmpty(req.getScheduleState())) {
            query.setParameter("scheduleState", req.getScheduleState());
        }

        if (!ObjectUtils.isEmpty(req.getRecurrenceType())) {
            query.setParameter("recurrenceType", req.getRecurrenceType());
        }

        if (req.getFromDate() != null) {
            query.setParameter("fromDate", req.getFromDate());
        }

        if (req.getToDate() != null) {
            query.setParameter("toDate", req.getToDate());
        }
    }

    private Long toLong(Object value) {
        return value != null ? ((Number) value).longValue() : 0L;
    }
}
