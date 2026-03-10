//package com.minhpt.smart_wallet_service.util;
//
//import com.minhpt.smart_wallet_service.constant.Constant;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EntityUtil{
//    private EntityUtil() {
//        throw new IllegalStateException("EntityUtil class");
//    }
//    public static <T> List<Specification<T>> getDefaultSearchSpecList() {
//        List<Specification<T>> specList = new ArrayList<>();
//        specList.add(SearchUtil.eq("isDeleted", Constant.NOT_DELETE));
//        return specList;
//    }
//
//    public static <T> List<Specification<T>> getDefaultTenantSearchSpecList() {
//        List<Specification<T>> specList = new ArrayList<>();
//        specList.add(SearchUtil.eq("isDeleted", Constant.NOT_DELETE));
////        specList.add(SearchUtil.eq("tenantId", TenantContext.getCurrentTenant()));
//        return specList;
//    }
//
//    public static <T> Specification<T> getSpecification(List<Specification<T>> specList) {
//        Specification<T> spec = specList.get(0);
//        if (specList.size() > 1) {
//            for (int i = 1; i < specList.size(); i++) {
//                spec = spec.and(specList.get(i));
//            }
//        }
//        return spec;
//    }
//}
