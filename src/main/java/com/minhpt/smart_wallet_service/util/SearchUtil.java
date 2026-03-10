package com.minhpt.smart_wallet_service.util;

import com.minhpt.smart_wallet_service.constant.Constant;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings({"serial", "unchecked", "rawtypes"})
public class SearchUtil {
    private SearchUtil() {
        throw new IllegalStateException("SearchUtil class");
    }
    public static Pageable getPageableFromParam(Integer page, Integer size, String sort, Constant.SortOrderEnum order) {
        Sort sortRequest;
        if (sort != null) {
            sortRequest = Sort.by(order == Constant.SortOrderEnum.ASC ? Direction.ASC : Direction.DESC, sort);
        } else {
            sortRequest = Sort.unsorted();
        }
        if (size == null || size > Constant.DEFAULT_PAGE_SIZE_MAX) {
            size = Constant.DEFAULT_PAGE_SIZE_MAX;
        }
        return PageRequest.of(page, size, sortRequest);
    }

    public static Pageable getDefaultPageableFromParam() {
        Sort sortRequest;
        sortRequest = Sort.unsorted();
        int size = Constant.DEFAULT_PAGE_SIZE_MAX;
        int page = 0;
        return PageRequest.of(page, size, sortRequest);
    }

    public static <T> Specification<T> like(String fieldName, String value) {
        return (root, query, cb) -> {
            if (value != null) {
                return cb.like(cb.lower(root.get(fieldName)), value.toLowerCase());
            }
            return cb.conjunction();
        };
    }

//    public static <R, F> Specification<R> in(String fieldName, List<F> filterList) {
//        return (root, query, cb) -> {
//            if (filterList != null && !filterList.isEmpty()) {
//                In<F> inClause = cb.in(root.get(fieldName));
//                filterList.forEach(inClause::value);
//                return inClause;
//            }
//            return cb.conjunction();
//        };
//    }

    public static <T> Specification<T> eq(String fieldName, Object value) {
        return (root, query, cb) -> {
            if (value != null) {
                return cb.equal(root.get(fieldName), value);
            }
            return cb.conjunction();
        };
    }
}
