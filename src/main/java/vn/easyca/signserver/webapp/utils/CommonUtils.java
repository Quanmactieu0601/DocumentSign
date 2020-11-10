package vn.easyca.signserver.webapp.utils;


import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

public class CommonUtils {
    public static void setParams(Query query, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            Set<Map.Entry<String, Object>> set = params.entrySet();
            for (Map.Entry<String, Object> obj : set) {
                if (obj.getValue() == null)
                    query.setParameter(obj.getKey(), "");
                else
                    query.setParameter(obj.getKey(), obj.getValue());
            }
        }
    }

    public static boolean isNullOrEmptyProperty(String string) {
        return string == null || string.isEmpty() || string.equals("null");
    }

    public static void setParamsWithPageable(@NotNull Query query, Map<String, Object> params, @NotNull Pageable pageable, @NotNull Number total) {
        if (params != null && !params.isEmpty()) {
            Set<Map.Entry<String, Object>> set = params.entrySet();
            for (Map.Entry<String, Object> obj : set) {
                query.setParameter(obj.getKey(), obj.getValue());
            }
        }
//        if (total.intValue() < (int)pageable.getOffset()) {
//            pageable = PageRequest.of(0, pageable.getPageSize(), pageable.getSort());
//        }
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
    }

}
