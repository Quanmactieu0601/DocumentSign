package vn.easyca.signserver.webapp.repository.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;
import vn.easyca.signserver.webapp.utils.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Transaction> findByFilter(Pageable pageable ,String api, String triggerTime, String code, String message, String data, String type, String host, String method, String createdBy ) {
        Map<String, Object> params = new HashMap<>();
        List transactionList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM transaction a ");
        sqlBuilder.append("left join jhi_user b " +
            "on a.created_by = b.login ");
        sqlBuilder.append("WHERE 1 = 1 ");

        if (!QueryUtils.isNullOrEmptyProperty(api)) {
            sqlBuilder.append("AND a.api like :api ");
            params.put("api", "%" + api + "%");
        }
//        if (!QueryUtils.isNullOrEmptyProperty(triggerTime)) {
//            sqlBuilder.append("AND a.trigger_Time like :triggerTime ");
//            params.put("triggerTime", "%" + triggerTime + "%");
//        }
        if (!QueryUtils.isNullOrEmptyProperty(code)) {
            sqlBuilder.append("AND a.code like :code ");
            params.put("code", "%" + code + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(message)) {
            sqlBuilder.append("AND a.message like :message ");
            params.put("message", "%" + message + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(data)) {
            sqlBuilder.append("AND a.data like :data ");
            params.put("data", "%" + data + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(type)) {
            sqlBuilder.append("AND a.type like :type ");
            params.put("type", "%" + type + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(host)) {
            sqlBuilder.append("AND a.host like :host ");
            params.put("host", "%" + host + "%");
        }
//        if (!QueryUtils.isNullOrEmptyProperty(method)) {
//            sqlBuilder.append("AND a.method like :method ");
//            params.put("method", "%" + method + "%");
//        }
        if (!QueryUtils.isNullOrEmptyProperty(createdBy)) {
            sqlBuilder.append("AND CONCAT(b.last_name,' ',b.first_name) like :createdBy ");
            params.put("createdBy", "%" + createdBy + "%");
        }

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            Query query = entityManager.createNativeQuery("SELECT *  " + sqlBuilder.toString(), Transaction.class );
            QueryUtils.setParamsWithPageable(query, params, pageable, total);
            transactionList = query.getResultList();
        }
        return new PageImpl<>(transactionList, pageable, total.longValue());
    }
}
