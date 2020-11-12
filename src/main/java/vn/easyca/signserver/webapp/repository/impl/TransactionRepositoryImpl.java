package vn.easyca.signserver.webapp.repository.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;
import vn.easyca.signserver.webapp.utils.CommonUtils;

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
    public Page<Transaction> findByFilter(Pageable pageable ,String api,String triggerTime ,String code, String message, String data, String type ,Long userID, String host, String method) {
        Map<String, Object> params = new HashMap<>();
        List<Transaction> transactionList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM transaction a ");
//        String sort = Common.addSort(pageable.getSort());

        sqlBuilder.append(" WHERE 1 = 1 ");
//        if (!Strings.isNullOrEmpty(account)) {
//            sqlBuilder.append("AND a.login like :login ");
//            params.put("login", "%" + account + "%");
//        }
        if (!CommonUtils.isNullOrEmptyProperty(api)) {
            sqlBuilder.append("AND a.api like :api ");
            params.put("api", "%" + api + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(triggerTime)) {
            sqlBuilder.append("AND a.trigger_Time like :triggerTime ");
            params.put("triggerTime", "%" + triggerTime + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(code)) {
            sqlBuilder.append("AND a.code like :code ");
            params.put("code", "%" + code + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(message)) {
            sqlBuilder.append("AND a.message like :message ");
            params.put("message", "%" + message + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(data)) {
            sqlBuilder.append("AND a.data like :data ");
            params.put("data", "%" + data + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(type)) {
            sqlBuilder.append("AND a.type like :type ");
            params.put("type", "%" + type + "%");
        }
//        if (!CommonUtils.isNullOrEmptyProperty(userID)) {
//            sqlBuilder.append("AND a.user_id like :userID ");
//            params.put("userID", "%" + userID + "%");
//        }
        if (!CommonUtils.isNullOrEmptyProperty(host)) {
            sqlBuilder.append("AND a.host like :host ");
            params.put("host", "%" + host + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(method)) {
            sqlBuilder.append("AND a.method like :method ");
            params.put("method", "%" + method + "%");
        }

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        CommonUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            Query query = entityManager.createNativeQuery("SELECT * " + sqlBuilder.toString(), Transaction.class);
            CommonUtils.setParamsWithPageable(query, params, pageable, total);
            transactionList = query.getResultList();
        }
        return new PageImpl<>(transactionList, pageable, total.longValue());

    }
}
