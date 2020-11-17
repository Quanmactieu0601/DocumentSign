package vn.easyca.signserver.webapp.repository.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
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

    protected UserEntity userEntity;

    @Override
    public Page<Transaction> findByFilter(Pageable pageable ,String api,String triggerTime ,String code, String message, String data, String type , String host, String method, String createdBy) {
        Map<String, Object> params = new HashMap<>();
        List<Transaction> transactionList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM transaction a ");
        sqlBuilder.append("left join jhi_user b\n" +
            "on a.created_by = b.login");
        sqlBuilder.append(" WHERE 1 = 1 ");

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
        if (!CommonUtils.isNullOrEmptyProperty(host)) {
            sqlBuilder.append("AND a.host like :host ");
            params.put("host", "%" + host + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(method)) {
            sqlBuilder.append("AND a.method like :method ");
            params.put("method", "%" + method + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(createdBy)) {
            sqlBuilder.append("AND b.login like :createdBy");
            params.put("createdBy", "%" + createdBy + "%");
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
