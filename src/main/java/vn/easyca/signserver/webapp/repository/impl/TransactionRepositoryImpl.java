package vn.easyca.signserver.webapp.repository.impl;

import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.utils.CommonUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TransactionDTO> findByFilter(Pageable pageable, String api, String triggerTime, String code, String message, String data, String type, String host, String method, String createdBy, String fullName) {
        Map<String, Object> params = new HashMap<>();
        List transactionList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
//        sqlBuilder.append("FROM transaction a ");
//        sqlBuilder.append("left join jhi_user b ");
//        sqlBuilder.append("on a.created_by = b.login ");
        sqlBuilder.append("from Transaction a ");
        sqlBuilder.append("left join UserEntity b ");
        sqlBuilder.append("on a.createdBy = b.login ");
        sqlBuilder.append("WHERE 1 = 1 ");

        if (!CommonUtils.isNullOrEmptyProperty(api)) {
            sqlBuilder.append("AND a.api like :api ");
            params.put("api", "%" + api + "%");
        }
//        if (!CommonUtils.isNullOrEmptyProperty(triggerTime)) {
//            sqlBuilder.append("AND a.trigger_Time like :triggerTime ");
//            params.put("triggerTime", "%" + triggerTime + "%");
//        }
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
        if (!CommonUtils.isNullOrEmptyProperty(fullName)) {
            sqlBuilder.append("AND CONCAT(b.lastName,' ',b.firstName) like :fullName ");
            params.put("fullName", "%" + fullName + "%");
        }

        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        CommonUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            Query transactionDTOList = entityManager.createQuery("select a.id as id, a.api as api,a.triggerTime as triggerTime,a.code as code,a.message as message,a.data as data,a.type as type,a.method as method,a.host as host, CONCAT(b.lastName,' ',b.firstName) as fullName " + sqlBuilder.toString())
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(TransactionDTO.class));
//            Query query = entityManager.createNativeQuery("select a.id, a.api,a.trigger_time,a.code,a.message,a.data,a.type,a.method,a.host, CONCAT(last_name,' ',first_name) as created_by " + sqlBuilder.toString(), TransactionDTO.class );
            CommonUtils.setParamsWithPageable(transactionDTOList, params, pageable, total);
            transactionList = transactionDTOList.getResultList();
        }
        return new PageImpl<>(transactionList, pageable, total.longValue());
    }
}
